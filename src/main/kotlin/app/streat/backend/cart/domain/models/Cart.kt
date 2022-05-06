package app.streat.backend.cart.domain.models

import app.streat.backend.core.util.CoreConstants.EMPTY
import javax.validation.constraints.Min

data class Cart(
    var shopId: String = EMPTY,
    @Min(0)
    var itemCount: Int = 0,
    var cartItems: MutableMap<String, CartItem> = mutableMapOf(),
    @Min(0)
    var totalCost: Double = 0.00
)

