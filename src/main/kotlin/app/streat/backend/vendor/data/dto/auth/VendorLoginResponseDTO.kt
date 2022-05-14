package app.streat.backend.vendor.data.dto.auth

data class VendorLoginResponseDTO(
    val vendorAccessToken: String,
    val isVerified: Boolean,
)