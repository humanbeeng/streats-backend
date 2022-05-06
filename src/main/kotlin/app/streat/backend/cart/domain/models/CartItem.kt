package app.streat.backend.cart.domain.models

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class CartItem(
    @NotBlank
    val shopId: String,
    @NotBlank
    val itemName: String,
    @Min(0)
    var quantity: Int = 0,
    @Min(0)
    val price: Double
)