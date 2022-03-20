package app.streat.backend.shop.services

import app.streat.backend.shop.data.repositories.StreatsShopRepository
import app.streat.backend.shop.domain.models.DishItem
import app.streat.backend.shop.domain.models.LocationEntity
import app.streat.backend.shop.domain.models.StreatsShop
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Service

@Service
class ShopService(private val repo: StreatsShopRepository) {

    fun createShop(streatsShop: StreatsShop): StreatsShop {
        return repo.save(streatsShop)
    }

    fun getAllShops(): List<StreatsShop> {
        return repo.findAll()
    }


    fun getShop(shopId: String): StreatsShop {
        return repo.findStreatsShopById(shopId)
    }

    fun searchByShopName(shopName: String): List<StreatsShop> {
        return repo.findByShopName(shopName)
    }


    /**
     * TODO : Move this to admin service and refactor
     */
    fun createNewDummyShop(): StreatsShop {
        return repo.save(
            StreatsShop(
                shopName = "Test Shop",
                shopOwnerPhoneNumber = "9876543231",
                location = LocationEntity(
                    GeoJsonPoint(
                        100.4, 98.3
                    ),
                    zipCode = "Ashok nagar"
                ),
                isShopOpen = true,
                shopItems = mutableListOf(DishItem(id = ObjectId(), name = "Masala Puri", price = 40)),
                isTakeawaySupported = true,
            )
        )
    }

}