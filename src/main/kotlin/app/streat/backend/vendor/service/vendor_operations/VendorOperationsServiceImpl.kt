package app.streat.backend.vendor.service.vendor_operations

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.order.domain.model.status.OrderStatus
import app.streat.backend.order.service.OrderService
import app.streat.backend.shop.domain.models.StreatsShop
import app.streat.backend.shop.services.ShopService
import app.streat.backend.vendor.domain.models.ShopStatus
import app.streat.backend.vendor.domain.models.home.VendorHome
import app.streat.backend.vendor.service.vendor_management.StreatsVendorManagementService
import org.springframework.stereotype.Service

/**
 * TODO : Rework this service
 *
 * TODO : Add check whether vendor is the owner of the shop which is requested.
 *         This can be done by adding vendor filter which filters for /vendor and /shop(probably)
 */

@Service
class VendorOperationsServiceImpl(
    private val shopService: ShopService,
    private val orderService: OrderService,
    private val streatsVendorManagementService: StreatsVendorManagementService,
    private val streatsUserService: StreatsUserService
) : VendorOperationsService {
    override fun fetchHome(vendorId: String): VendorHome {
        return try {
            val vendor = streatsVendorManagementService.getStreatsVendorByVendorId(vendorId)
            val shopId = vendor.shopId
            val shop = shopService.findShopByShopId(shopId)

            VendorHome(
                shopId = shopId,
                vendorId = vendorId,
                ongoingOrders = shop.ongoingOrders.toList(),
                shopName = shop.shopName,
                shopStatus = parseBooleanAsShopStatus(shop.isShopOpen)
            )

        } catch (e: Exception) {
            throw e
        }
    }

    private fun parseBooleanAsShopStatus(isShopOpen: Boolean): ShopStatus {
        return if (isShopOpen)
            ShopStatus.OPEN
        else ShopStatus.CLOSED
    }


    override fun updateShopStatus(vendorId: String, shopStatus: ShopStatus): StreatsShop {
        return try {
//            Clear all ongoing orders when shop should close
            if (shopStatus == ShopStatus.CLOSED) {
                shopService.clearAllCurrentDayOrders(vendorId)
            }
            shopService.updateShopStatus(vendorId, shopStatus)
        } catch (e: Exception) {
//            TODO : Add loggers and model to specific exception types
            throw e
        }
    }

    override fun fetchCurrentDayOrders(vendorId: String): List<Order> {
        try {
            val shop = shopService.findShopByVendorId(vendorId)
            return shop.ongoingOrders
        } catch (e: Exception) {
            throw e
        }

    }

    //    TODO : Refactor : Run by chaining Kotlin functions
    override fun updateOrderStatus(orderId: String, orderStatus: OrderStatus): Order {
        return try {
//        Step 1:
            orderService.updateOrderStatusInOrderRepo(orderId, orderStatus).let {

//        Step 2:
                shopService.updateOrderStatusInOngoingOrdersList(orderId, orderStatus)

//        Step 3:
                streatsUserService.updateOrderStatusInOrderHistory(it.userId, orderId, orderStatus)
                it
            }
        } catch (e: Exception) {
            throw e
        }
    }

}