package com.streats.backendphaseone.shop.domain.models

import org.springframework.data.annotation.Id

data class DishItem(
    @Id
    val id: String?,
    val name: String,
    val price: Int,
) {

}
