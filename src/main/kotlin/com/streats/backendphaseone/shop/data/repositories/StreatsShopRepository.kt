package com.streats.backendphaseone.shop.data.repositories

import com.streats.backendphaseone.shop.domain.models.StreatsShop
import org.springframework.data.mongodb.repository.MongoRepository

interface StreatsShopRepository : MongoRepository<StreatsShop, String> {

    fun findByShopName(shopName: String): List<StreatsShop>

    fun findStreatsShopById(shopId: String): StreatsShop

}