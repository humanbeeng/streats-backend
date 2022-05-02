package app.streat.backend.auth.data.dto

import app.streat.backend.auth.domain.models.location.CurrentLocationCoordinates
import app.streat.backend.auth.domain.models.login_request.LoginRequest
import org.springframework.data.mongodb.core.geo.GeoJsonPoint


data class LoginRequestDTO(
    val currentLocationCoordinates: CurrentLocationCoordinates,
    val fcmToken: String,
    val idToken: String
) {
    fun toLoginRequest(): LoginRequest {
        return LoginRequest(
            GeoJsonPoint(currentLocationCoordinates.latitude, currentLocationCoordinates.longitude),
            fcmToken,
            idToken
        )
    }
}
