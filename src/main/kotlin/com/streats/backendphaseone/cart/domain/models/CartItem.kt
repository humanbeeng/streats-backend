package com.streats.backendphaseone.cart.domain.models

import org.bson.types.ObjectId

class CartItem(
    var quantity: Int,
    val id: ObjectId,
    name: String,
    price: Int
) {
}