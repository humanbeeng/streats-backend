package app.streat.backend.vendor.domain.models.streats_vendor

import app.streat.backend.auth.domain.models.user.User
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "vendors")
data class StreatsVendor(
    @Id val vendorId: String,

    override val username: String,
    override val email: String,

    val shopId: String,
    var vendorFcmToken: String,
    val roles: List<String>,
    val profilePictureUrl: String,
    val isActive: Boolean = false
) : User
