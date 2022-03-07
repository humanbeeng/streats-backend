package com.streats.backendphaseone.cart.data.dto

import org.bson.types.ObjectId

class CartRequestBody(
    val id:String,
    val dish_id:ObjectId,
    val quantity:Int
) {
}