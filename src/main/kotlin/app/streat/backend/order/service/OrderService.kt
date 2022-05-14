package app.streat.backend.order.service

import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.order.domain.model.order.OrderWithToken
import app.streat.backend.order.domain.model.status.OrderStatus

interface OrderService {

    fun findOrderByOrderId(orderId: String): Order

    fun getAllOrders(userId: String): List<Order>

    fun initiateOrder(userId: String): OrderWithToken

    fun verifyOrderPaymentAndPlaceOrder(orderPaymentVerificationRequestParams: LinkedHashMap<String, String>): Boolean

    fun deleteAllOrders(userId: String)

    fun updateOrderStatusInOrderRepo(orderId: String, orderStatus: OrderStatus): Order


}