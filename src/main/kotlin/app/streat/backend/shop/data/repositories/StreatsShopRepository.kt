package app.streat.backend.shop.data.repositories

import app.streat.backend.shop.domain.models.StreatsShop
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface StreatsShopRepository : MongoRepository<StreatsShop, String> {

    fun findByShopName(shopName: String): List<StreatsShop>

    fun findStreatsShopByVendorId(vendorId: String): Optional<StreatsShop>

    fun findStreatsShopByShopId(shopId: String): Optional<StreatsShop>


    fun findByFeaturedIsTrue(): List<StreatsShop>

    fun findByCoordinatesNear(coordinates: GeoJsonPoint): List<StreatsShop>

}