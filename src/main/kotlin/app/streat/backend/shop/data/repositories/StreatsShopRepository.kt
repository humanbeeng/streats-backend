package app.streat.backend.shop.data.repositories

import app.streat.backend.shop.domain.models.StreatsShop
import org.springframework.data.mongodb.repository.MongoRepository

interface StreatsShopRepository : MongoRepository<StreatsShop, String> {

    fun findByShopName(shopName: String): List<StreatsShop>

    fun findStreatsShopById(shopId: String): StreatsShop

}