package app.streat.backend.shop.services

import app.streat.backend.order.domain.model.status.OrderStatus
import app.streat.backend.shop.domain.models.StreatsShop
import app.streat.backend.vendor.domain.models.ShopStatus
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

interface ShopService {

    fun findShopByVendorId(vendorId: String): StreatsShop

    fun getAllShops(): List<StreatsShop>

    fun getAllNearbyShops(geoJsonPoint: GeoJsonPoint): List<StreatsShop>

    fun updateShopStatus(vendorId: String, shopStatus: ShopStatus): StreatsShop

    fun updateOrderStatusInOngoingOrdersList(orderId: String, orderStatus: OrderStatus)


    fun clearAllCurrentDayOrders(vendorId: String): StreatsShop

    fun getFeaturedShops(): List<StreatsShop>

    fun getShopById(shopId: String): StreatsShop

    fun findShopByShopName(shopName: String): List<StreatsShop>

    fun findShopsByZipCode(zipCode: String): List<StreatsShop>

    fun findShopByShopId(shopId: String): StreatsShop


    /**
     * TODO : Delete these functions
     */
    fun deleteAllShops()

    fun addDummyShops(): List<StreatsShop>


}