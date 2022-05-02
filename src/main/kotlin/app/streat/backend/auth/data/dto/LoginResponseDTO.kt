package app.streat.backend.auth.data.dto


data class LoginResponseDTO(
    val accessToken: String,
    val isVerified: Boolean,
)