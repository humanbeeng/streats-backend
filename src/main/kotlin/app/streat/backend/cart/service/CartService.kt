package app.streat.backend.cart.service

import app.streat.backend.cart.data.dto.CartRequestDTO
import app.streat.backend.cart.domain.models.Cart
import org.springframework.stereotype.Service

@Service
interface CartService {
    fun getUserCart(userId: String): Cart

    fun addToCart(userId: String, cartRequestDTO: CartRequestDTO): Cart

    fun removeFromCart(userId: String, cartRequestDTO: CartRequestDTO): Cart

    fun getUserCartItemCount(userId: String) : Int

}