package app.streat.backend.auth.data.dto

import app.streat.backend.auth.domain.models.auth_request.AuthRequest
import app.streat.backend.auth.domain.models.location.CurrentLocationCoordinates
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

data class AuthRequestDTO(
    val accessToken: String,
    val currentLocationCoordinates: CurrentLocationCoordinates,
    val fcmToken: String
) {
    fun toAuthRequest(): AuthRequest {
        return AuthRequest(
            accessToken,
            fcmToken,
            GeoJsonPoint(currentLocationCoordinates.latitude, currentLocationCoordinates.longitude)
        )
    }


}