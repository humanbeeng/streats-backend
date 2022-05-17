package app.streat.backend.vendor.data.dto.status

import app.streat.backend.vendor.domain.models.ShopStatus

data class ShopStatusDTO(
    val shopStatus: String
) {
    fun toShopStatus(): ShopStatus {
        return when (shopStatus) {
            ShopStatus.CLOSED.name -> ShopStatus.CLOSED
            ShopStatus.OPEN.name -> ShopStatus.OPEN
            else -> throw Exception("Invalid ShopStatus")
        }
    }
}