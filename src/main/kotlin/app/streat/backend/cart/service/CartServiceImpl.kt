package app.streat.backend.cart.service

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.cart.data.dto.CartDTO
import app.streat.backend.cart.domain.models.Cart
import app.streat.backend.cart.domain.models.CartItem
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
     * TODO : Add checking of same shop dishItem
     */
    override fun addToCart(userId: String, cartDTO: CartDTO): Cart {
        val user = userService.getStreatsCustomer(userId)
        val dishId = cartDTO.dishId
        val shopId = cartDTO.shopId
        val dishItem = shopService.getShopById(shopId).shopItems[dishId]
            ?: throw NoSuchElementException("No shops found with given shop ID")

//        Check whether cartItem is present
        if (user.cart.cartItems.containsKey(dishId)) {
            val cartItem =
                user.cart.cartItems[dishId]
                    ?: throw NoSuchElementException("Something went wrong while fetching cart items")
            cartItem.quantity = cartItem.quantity.plus(1)
            user.cart.cartItems[dishId] = cartItem
            user.cart.totalCost = user.cart.totalCost + dishItem.price
        } else {
            user.cart
                .cartItems[dishId] =
                CartItem(shopId, dishItem.dishName, 1, dishItem.price)
            user.cart
                .totalCost = user.cart.totalCost + dishItem.price
            user.cart
                .itemCount = user.cart.cartItems.size
        }

        return userService.updateStreatsCustomer(user).cart

    }

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
                throw NoSuchElementException("No item present for given dishId")
            }
        } else {
            throw NoSuchElementException("No item present for given dishId")
        }
        user.cart.itemCount = user.cart.cartItems.size

        return userService.updateStreatsCustomer(user).cart

    }

    override fun clearCart(userId: String): Cart {
        val user = userService.getStreatsCustomer(userId)
        user.cart.cartItems = mutableMapOf()
        user.cart.totalCost = 0.00
        user.cart.itemCount = 0

        return userService.updateStreatsCustomer(user).cart

    }


}