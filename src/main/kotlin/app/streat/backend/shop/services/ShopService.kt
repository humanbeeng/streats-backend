package app.streat.backend.shop.services

import app.streat.backend.shop.domain.models.StreatsShop
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

interface ShopService {

    fun getAllShops(): List<StreatsShop>

    fun getAllNearbyShops(geoJsonPoint: GeoJsonPoint): List<StreatsShop>

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