package app.streat.backend.cart.controllers

import app.streat.backend.cart.data.dto.CartRequestDTO
import app.streat.backend.cart.domain.models.Cart
import app.streat.backend.cart.service.CartService
import app.streat.backend.core.util.JWTUtil
import app.streat.backend.core.util.NetworkConstants.HEADER_AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart")
class CartController(
    private val jwtUtil: JWTUtil, private val cartService: CartService
) {

    @GetMapping
    fun getCartItems(
        @RequestHeader(HEADER_AUTHORIZATION) accessToken: String,
    ): ResponseEntity<Cart> {
        return try {
            val userCart = cartService.getUserCart(jwtUtil.getId(accessToken))
            ResponseEntity.ok(userCart)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }


    }


    @PostMapping
    fun addToCart(
        @RequestHeader(HEADER_AUTHORIZATION) accessToken: String, @RequestBody cartRequestDTO: CartRequestDTO
    ): ResponseEntity<Cart> {

        return try {
            val updatedCart = cartService.addToCart(jwtUtil.getId(accessToken), cartRequestDTO)
            ResponseEntity.ok(updatedCart)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }


    @PostMapping("/remove")
    fun deleteCartItem(
        @RequestHeader(HEADER_AUTHORIZATION) accessToken: String, @RequestBody cartRequestDTO: CartRequestDTO
    ): ResponseEntity<Cart> {
        return try {
            val userId = jwtUtil.getId(accessToken)
            val updatedCart = cartService.removeFromCart(userId, cartRequestDTO)
            ResponseEntity.ok(updatedCart)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }


    @GetMapping("/count")
    fun getUserCartCount(
        @RequestHeader(HEADER_AUTHORIZATION) accessToken: String,
    ): ResponseEntity<Int> {
        return try {
            val userId = jwtUtil.getId(accessToken)
            val userCartItemCount = cartService.getUserCartItemCount(userId)
            ResponseEntity.ok(userCartItemCount)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

}