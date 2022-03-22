package app.streat.backend.auth.domain.usecase.models

import app.streat.backend.cart.domain.models.Cart
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "users")
data class StreatsCustomer(
    @Id
    val firebaseUID: String,

    override val email: String,
    override val username: String,

    @Field("profile_picture_url")
    val profilePictureUrl: String,
    val roles: List<String>,
    val cart: Cart
) : User