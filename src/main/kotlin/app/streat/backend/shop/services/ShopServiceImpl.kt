package app.streat.backend.shop.services

import app.streat.backend.order.domain.model.status.OrderStatus
import app.streat.backend.order.service.OrderService
import app.streat.backend.shop.data.repositories.StreatsShopRepository
import app.streat.backend.shop.domain.models.DishItem
import app.streat.backend.shop.domain.models.StreatsShop
import app.streat.backend.vendor.domain.models.ShopStatus
import app.streat.backend.vendor.service.vendor_management.StreatsVendorManagementService
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Service


//TODO : Extract exception types
//TODO : Rework vendor service server functions
@Service
class ShopServiceImpl(
    private val repo: StreatsShopRepository,
    private val orderService: OrderService,
    private val vendorManagementService: StreatsVendorManagementService,
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


    override fun getAllNearbyShops(coordinates: GeoJsonPoint): List<StreatsShop> {
        return repo.findByCoordinatesNear(coordinates)
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
            val shop = getShopByShopId(shopId)
            shop.ongoingOrders.find { it.orderId == orderId }?.orderStatus = orderStatus.name
            repo.save(shop)
        } catch (e: Exception) {
            throw Exception("Error occurred while updating order status in shop's ongoing orders list")
        }
    }


    override fun clearAllCurrentDayOrders(vendorId: String): StreatsShop {
        val vendor = vendorManagementService.getStreatsVendorByVendorId(vendorId)
        val shopId = vendor.shopId
        val shop = getShopByShopId(shopId)
        shop.ongoingOrders.clear()
        return repo.save(shop)
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


    override fun getShopByShopId(shopId: String): StreatsShop {
        return repo.findStreatsShopByShopId(shopId).orElseThrow { NoSuchElementException("No shop with shop ID found") }
    }

    /**
     * TODO : Move this to admin service and refactor
     */
    override fun addDummyShops(): List<StreatsShop> {

        val dummyShops = mutableListOf(
            StreatsShop(
                shopName = "Test Shop 1",
                shopOwnerPhoneNumber = "9876543231",
                coordinates = GeoJsonPoint(0.00, 0.00),
                zipCode = "577201",
                locationName = "Gopala Extension",
                isShopOpen = true,

                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00),
                    ObjectId().toString() to DishItem("Bhel Puri", 30.00),
                    ObjectId().toString() to DishItem("Dahi Puri", 60.50),
                    ObjectId().toString() to DishItem("Pakoda Masala", 50.00),
                    ObjectId().toString() to DishItem("Samosa", 30.00),

                    ),
                isTakeawaySupported = true,
                featured = false,
                vendorId = "j5XTAhDFCrTZjptddLAJP8UMK042"
            ),
            StreatsShop(
                shopName = "Test Shop 2",
                shopOwnerPhoneNumber = "9876543231",
                coordinates = GeoJsonPoint(0.00, 0.00),
                zipCode = "577201",
                locationName = "Alkola",
                isShopOpen = true,

                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ),
                isTakeawaySupported = true,
                featured = false,
                vendorId = "j5XTAhDFCrTZjptddLAJP8UMK042"
            ),
            StreatsShop(
                shopName = "Test Shop 3",
                shopOwnerPhoneNumber = "9876543231",
                coordinates = GeoJsonPoint(3.00, 0.00),
                zipCode = "577201",
                locationName = "Gopi Circle",
                isShopOpen = true,

                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ),
                isTakeawaySupported = true,
                featured = false,
                vendorId = "j5XTAhDFCrTZjptddLAJP8UMK042"
            ),
            StreatsShop(
                shopName = "Test Shop 4",
                shopOwnerPhoneNumber = "9876543231",
                coordinates = GeoJsonPoint(6.00, 2.00),
                zipCode = "577201",
                locationName = "Hudco Colony",
                isShopOpen = true,

                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ),
                isTakeawaySupported = true,
                featured = true,
                vendorId = "j5XTAhDFCrTZjptddLAJP8UMK042"
            ),
            StreatsShop(
                shopName = "Test Shop 5",
                shopOwnerPhoneNumber = "9876543231",
                coordinates = GeoJsonPoint(1.00, 5.00),
                zipCode = "577201",
                locationName = "LBS Nagar",
                isShopOpen = false,

                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ),
                isTakeawaySupported = true,
                featured = false,
                vendorId = "j5XTAhDFCrTZjptddLAJP8UMK042"
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