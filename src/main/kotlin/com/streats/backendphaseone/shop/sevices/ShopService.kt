package com.streats.backendphaseone.shop.sevices

import com.streats.backendphaseone.shop.data.dto.DishItemDTO
import com.streats.backendphaseone.shop.domain.models.StreatsShop
import com.streats.backendphaseone.shop.data.repositories.StreatsShopRepository
import com.streats.backendphaseone.shop.domain.models.DishItem
import com.streats.backendphaseone.shop.domain.usecase.ShopServiceUseCase
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShopService(private val repo: StreatsShopRepository, private val shopServiceUseCase: ShopServiceUseCase) {
    fun createShop(streatsShop: StreatsShop): StreatsShop {
        return repo.save(streatsShop)
    }

    fun getShops(): List<StreatsShop> {
        return repo.findAll()
    }

    fun addItem(item: DishItemDTO) {
        val shop = repo.findBy_id(item.shopID)
        val dishItem = shopServiceUseCase.addDishItem(item)
        shop.shop_items.add(dishItem)
        repo.save(shop)
    }

    fun getShop(shop_name: String): List<StreatsShop>? {
        return repo.findByshop_name(shop_name)
    }

    fun getShopByDishId(dish_id: ObjectId): Optional<StreatsShop> {
        return repo.findById(dish_id.toString())
    }

}