package app.streat.backend.order.service

import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.order.domain.model.order.OrderWithToken

interface OrderService {

    fun getAllOrders(userId: String): List<Order>

    fun initiateOrder(userId: String): OrderWithToken

    fun verifyOrderPayment(orderPaymentVerificationRequestParams: LinkedHashMap<String, String>): Boolean

    fun placeOrder(userId: String): Order

    fun deleteAllOrders(userId: String)
}