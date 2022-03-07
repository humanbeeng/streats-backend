package com.streats.backendphaseone.shop.domain.usecase

import com.streats.backendphaseone.shop.data.dto.DishItemDTO
import com.streats.backendphaseone.shop.domain.models.DishItem
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class ShopServiceUseCase {

    fun addDishItem(item: DishItemDTO): DishItem {
        val dish_item = DishItem(
            name = item.name,
            price = item.price,
            id = ObjectId()
        )
        return dish_item
    }


}