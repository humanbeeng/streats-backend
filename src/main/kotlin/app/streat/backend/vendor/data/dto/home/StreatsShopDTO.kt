package app.streat.backend.vendor.data.dto.home

import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.vendor.data.dto.status.ShopStatusDTO

data class StreatsShopDTO(
    val shopId: String,
    val shopName: String,
    val shopStatus: ShopStatusDTO,
    val vendorId: String,
    val ongoingOrders: List<Order>
)