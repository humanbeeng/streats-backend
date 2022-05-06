package app.streat.backend.auth.domain.models.user

import app.streat.backend.cart.domain.models.Cart
import app.streat.backend.core.util.CoreConstants.EMPTY
import app.streat.backend.order.domain.model.order.Order
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class StreatsCustomer(
    @Id
    val firebaseUID: String,
    var fcmTokenOfCurrentLoggedInDevice: String = EMPTY,

    var currentLocation: GeoJsonPoint,

    override val email: String,
    override val username: String,

    val profilePictureUrl: String,
    val roles: List<String>,
    val cart: Cart,
    val orders: MutableList<Order>
) : User