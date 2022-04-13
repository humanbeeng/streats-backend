package app.streat.backend.order.controller

import app.streat.backend.cart.service.exceptions.CartException
import app.streat.backend.core.util.JWTUtil
import app.streat.backend.order.data.dto.order_verification.OrderVerificationRequestDTO
import app.streat.backend.order.domain.model.Order
import app.streat.backend.order.domain.model.OrderWithToken
import app.streat.backend.order.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * TODO : Refactor placeOrder
 */


@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService, private val jwtUtil: JWTUtil) {



    @GetMapping("/initiate")
    fun initiateOrder(
        @RequestHeader("Authorization") accessToken: String
    ): ResponseEntity<OrderWithToken> {
        return try {
            val userId = jwtUtil.getUserId(accessToken)
            ResponseEntity.ok(orderService.initiateOrder(userId))

        } catch (e: Exception) {
            ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/verify")
    fun verifyOrder(
        @RequestHeader("Authorization") accessToken: String,
        @RequestBody orderVerificationRequestDTO: OrderVerificationRequestDTO
    ): ResponseEntity<Boolean> {
        val userId = jwtUtil.getUserId(accessToken)
        return ResponseEntity.ok(orderService.verifyOrderPayment(userId, orderVerificationRequestDTO))
    }


    @GetMapping
    fun getAllOrders(@RequestHeader("Authorization") accessToken: String): ResponseEntity<List<Order>> {

        return try {
            val userId = jwtUtil.getUserId(accessToken)
            ResponseEntity.ok(orderService.getAllOrders(userId))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }

    }

    @PostMapping
    fun placeOrder(@RequestHeader("Authorization") accessToken: String): ResponseEntity<Order> {
        return try {
            val userId = jwtUtil.getUserId(accessToken)
            ResponseEntity.ok(orderService.placeOrder(userId))
        } catch (e: CartException.EmptyCartException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    /**
     * Delete this service as it is used only for testing
     */
    @DeleteMapping
    fun deleteAllOrders(@RequestHeader("Authorization") accessToken: String): ResponseEntity<String> {
        return try {
            val userId = jwtUtil.getUserId(accessToken)
            orderService.deleteAllOrders(userId)
            ResponseEntity.ok("Cleared all orders")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
}