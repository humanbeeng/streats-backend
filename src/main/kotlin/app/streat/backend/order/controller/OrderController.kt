package app.streat.backend.order.controller

import app.streat.backend.core.util.JWTUtil
import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.order.domain.model.order.OrderWithToken
import app.streat.backend.order.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * TODO : Refactor placeOrder
 */


@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
    private val jwtUtil: JWTUtil
) {


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

    /**
     * Order Payment Verification Callback
     *
     * Callback is invoked only in the case of 'successful' payment. There might be duplicate incoming callbacks for
     * the same payment, hence the handler needs to be idempotent. The duplicate(invalid) callback will not contain
     * any 'incoming request params(i.e orderPaymentVerificationRequestParams) will be empty'
     */
    @PostMapping("/callback")
    fun verifyOrder(
        @RequestParam orderPaymentVerificationRequest: LinkedHashMap<String, String>
    ): ResponseEntity<Unit> {

        return try {
            if (orderPaymentVerificationRequest.isEmpty().not() &&
                orderService.verifyOrderPayment(orderPaymentVerificationRequest)
            ) {
                ResponseEntity.ok().build()
            } else {
                ResponseEntity.badRequest().build()
            }
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }

    }


    @GetMapping
    fun getAllOrders(
        @RequestHeader("Authorization") accessToken: String
    ): ResponseEntity<List<Order>> {

        return try {
            val userId = jwtUtil.getUserId(accessToken)
            ResponseEntity.ok(orderService.getAllOrders(userId))
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