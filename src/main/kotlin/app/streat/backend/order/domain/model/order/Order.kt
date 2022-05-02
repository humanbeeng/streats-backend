package app.streat.backend.order.domain.model.order

import app.streat.backend.cart.domain.models.CartItem
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * TODO : Match with client OrderDTO
 * TODO : Add shop device FCM Token
 */

@Document(collection = "orders")
data class Order(
    @Id
    val orderId: String,
    val shopId: String,
    val userId: String,
    val userFcmToken: String,

    val username: String,
    val itemCount: Int,
    val items: Map<String, CartItem>,
    val totalCost: Double,
    val orderedTime: String,
    val arrivalTime: String,
    var orderStatus: String,
    var paymentStatus: String,
    val orderedDate: String
)
