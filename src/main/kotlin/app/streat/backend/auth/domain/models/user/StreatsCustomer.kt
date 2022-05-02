package app.streat.backend.auth.domain.models.user

import app.streat.backend.cart.domain.models.Cart
import app.streat.backend.order.domain.model.order.Order
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "users")
data class StreatsCustomer(
    @Id
    val firebaseUID: String,
    var fcmTokenOfCurrentLoggedInDevice: String = "",

    var currentLocation: GeoJsonPoint,

    override val email: String,
    override val username: String,

    @Field("profile_picture_url")
    val profilePictureUrl: String,
    val roles: List<String>,
    val cart: Cart,
    val orders: MutableList<Order>
) : User