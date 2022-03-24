package app.streat.backend.order.domain.model

import app.streat.backend.cart.domain.models.CartItem
import org.bson.types.ObjectId

data class Order(
    val orderId: String = ObjectId().toString(),
    val shopId: String,
    val userId: String,
    val username: String,
    val itemCount: Int,
    val items: Map<String, CartItem>,
    val totalCost: Double,
    val orderedTime: String,
    val arrivalTime: String,
    val orderStatus: String,
    val orderedDate: String
)
