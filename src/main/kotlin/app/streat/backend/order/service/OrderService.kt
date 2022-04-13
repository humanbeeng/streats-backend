package app.streat.backend.order.service

import app.streat.backend.order.data.dto.order_verification.OrderVerificationRequestDTO
import app.streat.backend.order.domain.model.Order
import app.streat.backend.order.domain.model.OrderWithToken

interface OrderService {

    fun getAllOrders(userId: String): List<Order>

    fun initiateOrder(userId: String): OrderWithToken

    fun verifyOrderPayment(userId: String, orderVerificationRequestDTO: OrderVerificationRequestDTO): Boolean

    fun placeOrder(userId: String): Order

    fun deleteAllOrders(userId: String)
}