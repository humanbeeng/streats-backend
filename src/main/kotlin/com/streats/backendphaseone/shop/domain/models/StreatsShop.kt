package com.streats.backendphaseone.shop.domain.models

import com.streats.backendphaseone.shop.models.LocationEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "shops")
class StreatsShop(
    @Id
    val id: String?,

    val shop_name: String,
    val shop_owner_phone_number: String,

    val location: LocationEntity,
    val shop_items: MutableList<DishItem?>,
    val is_takeaway_supported: Boolean,
    val is_shop_open: Boolean
) {
}

