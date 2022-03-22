package app.streat.backend.cart.domain.models

data class Cart(
    var itemCount: Int = 0,
    var cartItems: MutableMap<String, CartItem> = mutableMapOf(),
    var totalCost: Double = 0.00
)

