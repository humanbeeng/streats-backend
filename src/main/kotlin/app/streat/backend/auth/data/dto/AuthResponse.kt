package app.streat.backend.auth.data.dto


data class AuthResponse(
    val accessToken: String,
    val isVerified: Boolean
)