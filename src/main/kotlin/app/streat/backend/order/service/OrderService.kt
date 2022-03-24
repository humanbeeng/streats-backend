package app.streat.backend.order.service

import app.streat.backend.order.domain.model.Order

interface OrderService {

    fun getAllOrders(userId: String): List<Order>

    fun placeOrder(userId: String): Order

    fun deleteAllOrders(userId: String)
}