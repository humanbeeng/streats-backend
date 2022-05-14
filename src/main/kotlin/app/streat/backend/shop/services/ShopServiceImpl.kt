package app.streat.backend.shop.services

import app.streat.backend.order.domain.model.status.OrderStatus
import app.streat.backend.order.service.OrderService
import app.streat.backend.shop.data.repositories.StreatsShopRepository
import app.streat.backend.shop.domain.models.DishItem
import app.streat.backend.shop.domain.models.StreatsShop
import app.streat.backend.vendor.domain.models.ShopStatus
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Service


//TODO : Extract exception types
//TODO : Rework vendor service server functions
@Service
class ShopServiceImpl(
    private val repo: StreatsShopRepository,
    private val orderService: OrderService
) : ShopService {

    /**
     * TODO : Move this to admin service
     */
    fun createShop(streatsShop: StreatsShop): StreatsShop {
        return repo.save(streatsShop)
    }

    override fun findShopByVendorId(vendorId: String): StreatsShop {
        return repo.findStreatsShopByVendorId(vendorId)
            .orElseThrow { NoSuchElementException("No shop found with given vendorId") }
    }

    override fun getAllShops(): List<StreatsShop> {
        return repo.findAll()
    }


    override fun getAllNearbyShops(geoJsonPoint: GeoJsonPoint): List<StreatsShop> {
        return repo.findByLocationNear(geoJsonPoint)
    }

    override fun updateShopStatus(vendorId: String, shopStatus: ShopStatus): StreatsShop {
        try {
            val shop = findShopByVendorId(vendorId)
            when (shopStatus) {
                ShopStatus.OPEN -> shop.isShopOpen = true
                ShopStatus.CLOSED -> shop.isShopOpen = false
            }
            return repo.save(shop)
        } catch (e: Exception) {
            throw Exception("Something went while updating shop status")
        }
    }

    override fun updateOrderStatusInOngoingOrdersList(orderId: String, orderStatus: OrderStatus) {
        try {
            val order = orderService.findOrderByOrderId(orderId)
            val shopId = order.shopId
            val shop = getShopById(shopId)
            shop.ongoingOrders.find { it.orderId == orderId }?.orderStatus = orderStatus.name
            repo.save(shop)
        } catch (e: Exception) {
            throw Exception("Error occurred while updating order status in shop's ongoing orders list")
        }
    }


    override fun clearAllCurrentDayOrders(vendorId: String): StreatsShop {
        TODO("Not yet implemented")
    }


    override fun getFeaturedShops(): List<StreatsShop> {
        return repo.findByFeaturedIsTrue()
    }

    override fun findShopByShopId(shopId: String): StreatsShop {

        return repo.findStreatsShopByShopId(shopId)
            .orElseThrow { NoSuchElementException("No shop found with given shopId") }

    }


    override fun findShopByShopName(shopName: String): List<StreatsShop> {
        return repo.findByShopName(shopName)
    }


    override fun findShopsByZipCode(zipCode: String): List<StreatsShop> {
        return repo.findStreatsShopByZipcode(zipCode)
    }

    override fun getShopById(shopId: String): StreatsShop {
        return repo.findStreatsShopByShopId(shopId).orElseThrow { NoSuchElementException("No shop with shop ID found") }
    }

    /**
     * TODO : Move this to admin service and refactor
     */
    override fun addDummyShops(): List<StreatsShop> {

        val dummyShops = mutableListOf(
            StreatsShop(
                shopName = "Test Shop 1", shopOwnerPhoneNumber = "9876543231", location = GeoJsonPoint(
                    -73.93414657, 43.82302903
                ), zipcode = "100000", isShopOpen = true,

                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ), isTakeawaySupported = true, featured = false
            ),
            StreatsShop(
                shopName = "Test Shop 2", shopOwnerPhoneNumber = "9876543231", location = GeoJsonPoint(
                    -74.00310999999999, 40.7348888
                ), zipcode = "100004", isShopOpen = true, shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ), isTakeawaySupported = true, featured = false

            ),
            StreatsShop(
                shopName = "Test Shop 3", shopOwnerPhoneNumber = "9876543231", location = GeoJsonPoint(
                    -73.7522366, 40.7766941
                ), zipcode = "100005", isShopOpen = true, shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ), isTakeawaySupported = true, featured = true
            ),
            StreatsShop(
                shopName = "Test Shop 4",
                vendorId = "j5XTAhDFCrTZjptddLAJP8UMK042",
                shopOwnerPhoneNumber = "9876543231",
                location = GeoJsonPoint(
                    -73.99950489999999, 40.7169224
                ),
                zipcode = "100000",
                isShopOpen = false,
                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ),
                isTakeawaySupported = true,
                featured = false

            ),

            )
        return repo.saveAll(dummyShops)

    }

    /**
     * TODO : Delete this dangerous method
     */
    override fun deleteAllShops() {
        repo.deleteAll()
    }
}