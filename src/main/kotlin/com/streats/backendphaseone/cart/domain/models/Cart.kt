package com.streats.backendphaseone.cart.domain.models

data class Cart(
    val cartItems: MutableMap<String, CartItem>,
    val totalCost: Double
)

