package app.streat.backend.auth.domain.models.login_request

import org.springframework.data.mongodb.core.geo.GeoJsonPoint

data class LoginRequest(
    val currentLocation: GeoJsonPoint,
    val fcmToken: String,
    val idToken: String
)
