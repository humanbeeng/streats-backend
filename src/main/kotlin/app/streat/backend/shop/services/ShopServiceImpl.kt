package app.streat.backend.shop.services

import app.streat.backend.shop.data.repositories.StreatsShopRepository
import app.streat.backend.shop.domain.models.DishItem
import app.streat.backend.shop.domain.models.StreatsShop
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Service

@Service
class ShopServiceImpl(private val repo: StreatsShopRepository, private val mongoTemplate: MongoTemplate) : ShopService {

    /**
     * TODO : Move this to admin service
     */
    fun createShop(streatsShop: StreatsShop): StreatsShop {
        return repo.save(streatsShop)
    }

    override fun getAllShops(): List<StreatsShop> {
        return repo.findAll()
    }


    override fun getAllNearbyShops(geoJsonPoint: GeoJsonPoint): List<StreatsShop> {
        return repo.findByLocationNear(geoJsonPoint)
    }

    override fun getFeaturedShops(): List<StreatsShop> {
        return repo.findByFeaturedIsTrue()
    }

    override fun findShopByShopId(shopId: String): StreatsShop {

        return repo.findStreatsShopById(shopId)
            .orElseThrow { NoSuchElementException("No shop found with given shopId") }

    }

    override fun findShopByShopName(shopName: String): List<StreatsShop> {
        return repo.findByShopName(shopName)
    }


    override fun findShopsByZipCode(zipCode: String): List<StreatsShop> {
        return repo.findStreatsShopByZipcode(zipCode)
    }

    override fun getShopById(shopId: String): StreatsShop {
        return repo.findStreatsShopById(shopId).orElseThrow { NoSuchElementException("No shop with shop ID found") }
    }

    /**
     * TODO : Move this to admin service and refactor
     */
    override fun addDummyShops(): List<StreatsShop> {

        val dummyShops = mutableListOf(
            StreatsShop(
                shopName = "Test Shop 1",
                shopOwnerPhoneNumber = "9876543231",
                location = GeoJsonPoint(
                    -73.93414657, 43.82302903
                ),
                zipcode = "100000",
                isShopOpen = true,

                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ),
                isTakeawaySupported = true,
                featured = false
            ),
            StreatsShop(
                shopName = "Test Shop 2",
                shopOwnerPhoneNumber = "9876543231",
                location = GeoJsonPoint(
                    -74.00310999999999, 40.7348888
                ),
                zipcode = "100004",
                isShopOpen = true,
                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ),
                isTakeawaySupported = true,
                featured = false

            ),
            StreatsShop(
                shopName = "Test Shop 3",
                shopOwnerPhoneNumber = "9876543231",
                location = GeoJsonPoint(
                    -73.7522366, 40.7766941
                ),
                zipcode = "100005",
                isShopOpen = true,
                shopItems = mutableMapOf(
                    ObjectId().toString() to DishItem("Masala Puri", 90.00),
                    ObjectId().toString() to DishItem("Gobi Manchurian", 45.00)
                ),
                isTakeawaySupported = true,
                featured = true
            ),
            StreatsShop(
                shopName = "Test Shop 4",
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