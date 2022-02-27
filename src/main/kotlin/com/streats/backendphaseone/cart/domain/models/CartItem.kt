package com.streats.backendphaseone.cart.domain.models

import com.streats.backendphaseone.shop.domain.models.DishItem

class CartItem(
    var quantity: Int,
    dish_id: String?,
    name: String,
    price: Int
) {
}