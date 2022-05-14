package app.streat.backend.order.domain.model.order

import app.streat.backend.cart.domain.models.CartItem
import app.streat.backend.core.util.CoreConstants.EMPTY
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * TODO : Add vendor device FCM Token
 */

@Document(collection = "orders")
data class Order(
    @Id
    val orderId: String,
    val shopId: String,
    val userId: String,
    val userFcmToken: String,
    val vendorFcmToken: String = EMPTY,

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
