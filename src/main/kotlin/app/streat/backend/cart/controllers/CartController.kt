package app.streat.backend.cart.controllers

import app.streat.backend.auth.util.JWTUtil
import app.streat.backend.cart.data.dto.CartDTO
import app.streat.backend.cart.domain.models.Cart
import app.streat.backend.cart.service.CartService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart")
class CartController(
    private val jwtUtil: JWTUtil,
    private val cartService: CartService
) {

    @GetMapping
    fun getCartItems(
        @RequestHeader("Authorization") accessToken: String,
    ): ResponseEntity<Cart> {
        return try {
            val userCart = cartService.getUserCart(jwtUtil.getUserId(accessToken))
            ResponseEntity.ok(userCart)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }


    }


    @PostMapping
    fun addToCart(
        @RequestHeader("Authorization") accessToken: String,
        @RequestBody cartDTO: CartDTO
    ): ResponseEntity<Cart> {

        return try {
            val updatedCart = cartService.addToCart(jwtUtil.getUserId(accessToken), cartDTO)
            ResponseEntity.ok(updatedCart)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }


    @PostMapping("/remove")
    fun deleteCartItem(
        @RequestHeader("Authorization") accessToken: String,
        @RequestBody cartDTO: CartDTO
    ): ResponseEntity<Cart> {
        return try {
            val userId = jwtUtil.getUserId(accessToken)
            val updatedCart = cartService.removeFromCart(userId, cartDTO)
            ResponseEntity.ok(updatedCart)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * TODO : Remove this controller since we wont be calling this from client
     */

    @GetMapping("/clear")
    fun clearCart(@RequestHeader("Authorization") accessToken: String): ResponseEntity<String> {
        return try {
            val userId = jwtUtil.getUserId(accessToken)
            cartService.clearCart(userId)
            ResponseEntity.ok("Cart cleared")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}