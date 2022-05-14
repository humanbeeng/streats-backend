package app.streat.backend.vendor.data.dto.auth

data class VendorLoginRequestDTO(
    val vendorFcmToken: String,
    val idToken: String
)
