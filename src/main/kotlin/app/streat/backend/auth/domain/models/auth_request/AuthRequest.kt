package app.streat.backend.auth.domain.models.auth_request

import org.springframework.data.mongodb.core.geo.GeoJsonPoint

data class AuthRequest(
    val accessToken: String,
    val fcmToken: String,
    val currentLocation: GeoJsonPoint
)
