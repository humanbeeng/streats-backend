package app.streat.backend.cart.service

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.cart.data.dto.CartDTO
import app.streat.backend.cart.domain.models.Cart
import app.streat.backend.cart.domain.models.CartItem
import app.streat.backend.cart.service.exceptions.CartException
import app.streat.backend.core.util.CoreConstants.EMPTY
import app.streat.backend.shop.services.ShopService
import org.springframework.stereotype.Service


@Service
class CartServiceImpl(
    private val userService: StreatsUserService,
    private val shopService: ShopService
) : CartService {
    override fun getUserCart(userId: String): Cart {
        val user = userService.getStreatsCustomer(userId)
        return user.cart
    }

    /**
     *  AddToCart
     *
     *  Checks whether dishId is present from the given shopId
     *
     *  Checks whether the existing CartItems are from same shop
     *
     *  TODO : Needs refactoring
     *
     *  @param userId : String
     *  @param cartDTO : CartDTO
     *  @return Cart
     *  @throws CartException.ItemFromDifferentShopException
     *  @throws CartException.ItemNotFoundFromShopException
     *
     *
     */
    override fun addToCart(userId: String, cartDTO: CartDTO): Cart {
        val user = userService.getStreatsCustomer(userId)
        val dishId = cartDTO.dishId
        val shopId = cartDTO.shopId
        val dishItem = shopService.getShopById(shopId).shopItems[dishId]
            ?: throw CartException.ItemNotFoundFromShopException

        if (isCartItemAddable(shopId, user.cart).not()) {
            throw CartException.ItemFromDifferentShopException
        } else {
            if (user.cart.cartItems.containsKey(dishId)) {
                val cartItem = user.cart.cartItems[dishId]
                    ?: throw CartException.ItemFetchFromCartException
                cartItem.quantity = cartItem.quantity.plus(1)
                user.cart.cartItems[dishId] = cartItem
                user.cart.totalCost = user.cart.totalCost + dishItem.price
            } else {
                user.cart.shopId = shopId
                user.cart.cartItems[dishId] = CartItem(shopId, dishItem.dishName, 1, dishItem.price)
                user.cart.totalCost = user.cart.totalCost + dishItem.price
                user.cart.itemCount = user.cart.cartItems.size
            }

            return userService.updateStreatsCustomer(user).cart
        }


    }

    /**
     * RemoveFromCart
     *
     * Checks whether the to be removed CartItem is present in cart or not
     *
     * Resets shopId when all the cart items are removed
     *
     * TODO : Needs refactoring
     *
     * @param userId : String
     * @param cartDTO : CartDTO
     * @return Cart
     *
     *
     */

    override fun removeFromCart(userId: String, cartDTO: CartDTO): Cart {
        val dishId = cartDTO.dishId

        val user = userService.getStreatsCustomer(userId)

        if (user.cart.cartItems.containsKey(dishId)) {
            val cartItem = user.cart.cartItems[dishId]
            if (cartItem != null) {
                user.cart.totalCost = user.cart.totalCost - cartItem.price
                cartItem.quantity = cartItem.quantity.minus(1)
                if (cartItem.quantity == 0) {
                    user.cart.cartItems.remove(dishId)
                }
            } else {
                throw CartException.ItemFetchFromCartException
            }

        } else {
            throw CartException.ItemFetchFromCartException
        }
        user.cart.itemCount = user.cart.cartItems.size
        if (user.cart.itemCount == 0) {
            user.cart.shopId = ""
        }
        return userService.updateStreatsCustomer(user).cart

    }

    override fun clearCart(userId: String) {
        val user = userService.getStreatsCustomer(userId)
        user.cart.cartItems = mutableMapOf()
        user.cart.totalCost = 0.00
        user.cart.itemCount = 0
        user.cart.shopId = EMPTY

        userService.updateStreatsCustomer(user)

    }

    private fun isCartItemFromDifferentShop(shopId: String, cart: Cart): Boolean {
        return shopId == cart.shopId && cart.shopId.isNotBlank()
    }

    private fun isCartItemAddable(shopId: String, cart: Cart): Boolean {
        return isCartItemFromDifferentShop(shopId, cart) || cart.shopId.isBlank()
    }


}