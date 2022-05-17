package app.streat.backend.vendor.domain.models.home

import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.vendor.domain.models.ShopStatus

data class VendorHome(
    val shopName: String,
    val shopId: String,
    val shopStatus: ShopStatus,
    val vendorId: String,
    val ongoingOrders: List<Order>
)