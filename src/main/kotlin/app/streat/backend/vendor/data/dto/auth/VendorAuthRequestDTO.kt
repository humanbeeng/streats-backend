package app.streat.backend.vendor.data.dto.auth

data class VendorAuthRequestDTO(
    val vendorAccessToken: String,
    val vendorFcmToken: String
)