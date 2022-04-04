package app.streat.backend.shop.data.repositories

import app.streat.backend.shop.domain.models.StreatsShop
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface StreatsShopRepository : MongoRepository<StreatsShop, String> {

    fun findByShopName(shopName: String): List<StreatsShop>

    fun findStreatsShopByShopId(shopId: String): Optional<StreatsShop>

    fun findStreatsShopByZipcode(zipCode: String): List<StreatsShop>

    fun findByFeaturedIsTrue(): List<StreatsShop>

    fun findByLocationNear(geoJsonPoint: GeoJsonPoint): List<StreatsShop>

    fun findAllByShopItems(dishId: String): List<StreatsShop>
}