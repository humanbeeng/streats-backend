package app.streat.backend.cart.service

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.cart.data.dto.CartRequestDTO
import app.streat.backend.cart.domain.models.Cart
import app.streat.backend.cart.domain.models.CartItem
import app.streat.backend.cart.service.exceptions.CartException
import app.streat.backend.shop.services.ShopService
import org.springframework.stereotype.Service


@Service
class CartServiceImpl(
    private val userService: StreatsUserService, private val shopService: ShopService
) : CartService {
    override fun getUserCart(userId: String): Cart {
        val user = userService.getStreatsCustomerById(userId)
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
     *  @param cartRequestDTO : CartDTO
     *  @return Cart
     *  @throws CartException.ItemFromDifferentShopException
     *  @throws CartException.ItemNotFoundFromShopException
     *
     *
     */
    override fun addToCart(userId: String, cartRequestDTO: CartRequestDTO): Cart {
        val user = userService.getStreatsCustomerById(userId)
        val dishId = cartRequestDTO.dishId
        val shopId = cartRequestDTO.shopId
        val shop = shopService.getShopByShopId(shopId)
        val dishItem = shop.shopItems[dishId] ?: throw CartException.ItemNotFoundFromShopException

        if (isCartItemAddable(shopId, user.cart).not()) {
            throw CartException.ItemFromDifferentShopException
        } else {
            if (user.cart.cartItems.containsKey(dishId)) {
                val cartItem = user.cart.cartItems[dishId] ?: throw CartException.ItemFetchFromCartException
                cartItem.quantity = cartItem.quantity.plus(1)
                user.cart.cartItems[dishId] = cartItem
                user.cart.totalCost = user.cart.totalCost + dishItem.price
            } else {
                user.cart.shopName = shop.shopName
                user.cart.shopId = shopId
                user.cart.cartItems[dishId] = CartItem(
                    shopId = shopId,
                    itemName = dishItem.dishName,
                    quantity = 1,
                    price = dishItem.price,
                )
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
     * @param cartRequestDTO : CartDTO
     * @return Cart
     *
     *
     */

    override fun removeFromCart(userId: String, cartRequestDTO: CartRequestDTO): Cart {
        val dishId = cartRequestDTO.dishId

        val user = userService.getStreatsCustomerById(userId)

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

    override fun getUserCartItemCount(userId: String): Int {
        val user = userService.getStreatsCustomerById(userId)
        return user.cart.itemCount
    }


    private fun isCartItemFromDifferentShop(shopId: String, cart: Cart): Boolean {
        return shopId == cart.shopId && cart.shopId.isNotBlank()
    }

    private fun isCartItemAddable(shopId: String, cart: Cart): Boolean {
        return isCartItemFromDifferentShop(shopId, cart) || cart.shopId.isBlank()
    }


}