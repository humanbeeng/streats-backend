package app.streat.backend.order.data.repository

import app.streat.backend.order.domain.model.order.Order
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepository : MongoRepository<Order, String> {
    fun findOrderByOrderId(orderId: String) : Order
}