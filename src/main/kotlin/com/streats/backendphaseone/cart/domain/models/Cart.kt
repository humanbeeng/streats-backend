package com.streats.backendphaseone.cart.domain.models

class Cart(
    val cartItems: MutableMap<String, CartItem>,
    val totalCost: Double
) {
}


