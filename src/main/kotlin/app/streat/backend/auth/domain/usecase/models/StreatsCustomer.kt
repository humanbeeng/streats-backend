package app.streat.backend.auth.domain.usecase.models

import app.streat.backend.cart.domain.models.CartItem
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class StreatsCustomer(
    @Id
    val firebaseUID: String,

    override val email: String,
    override val username: String,

    val profilePictureUrl: String,
    val roles: List<String>,
    val cart: MutableMap<ObjectId, CartItem>,
//    val orders:MutableList<Order>List<CartItem>
//    val dish i
) : User