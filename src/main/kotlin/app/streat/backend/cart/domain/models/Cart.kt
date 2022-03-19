package app.streat.backend.cart.domain.models

data class Cart(
    val cartItems: MutableMap<String, CartItem>,
    val totalCost: Double
)

