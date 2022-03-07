package com.streats.backendphaseone.shop.data.repositories

import com.streats.backendphaseone.shop.domain.models.StreatsShop
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface StreatsShopRepository : MongoRepository<StreatsShop, String> {
    //    fun findByLocationNear(point: Point, distance: Distance): List<StreatsShop>
    @Query("{'shop_name':{\$regex:?0,\$options:'i'}}")
    fun findByshop_name(shop_name: String): List<StreatsShop>

    fun findBy_id(dish_id:String): StreatsShop
}