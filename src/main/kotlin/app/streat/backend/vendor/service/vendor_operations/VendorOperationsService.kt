package app.streat.backend.vendor.service.vendor_operations

import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.order.domain.model.status.OrderStatus
import app.streat.backend.shop.domain.models.StreatsShop
import app.streat.backend.vendor.domain.models.ShopStatus

interface VendorOperationsService {

    fun updateShopStatus(vendorId: String, shopStatus: ShopStatus): StreatsShop

    fun fetchCurrentDayOrders(vendorId: String): List<Order>

    fun updateOrderStatus(orderId: String, orderStatus: OrderStatus): Order

}