package com.streats.backendphaseone.shop.sevices

import com.streats.backendphaseone.shop.domain.models.StreatsShop
import com.streats.backendphaseone.shop.data.repositories.StreatsShopRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShopService(private val repo: StreatsShopRepository) {
    fun createShop(streatsShop: StreatsShop): StreatsShop {
        return repo.save(streatsShop)
    }

    fun getShops(): List<StreatsShop> {
        return repo.findAll()
    }

    fun getShop(shop_name: String): List<StreatsShop>? {
        return repo.findByshop_name(shop_name)
    }

    fun getShopByDishId(dish_id:String): Optional<StreatsShop> {
        return repo.findById(dish_id)
    }

}