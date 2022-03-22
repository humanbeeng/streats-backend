package app.streat.backend.cart.service

import app.streat.backend.cart.data.dto.CartDTO
import app.streat.backend.cart.domain.models.Cart
import org.springframework.stereotype.Service

@Service
interface CartService {
    fun getUserCart(userId: String): Cart

    fun addToCart(userId: String, cartDTO: CartDTO): Cart

    fun removeFromCart(userId: String, cartDTO: CartDTO): Cart

    fun clearCart(userId: String): Cart

}