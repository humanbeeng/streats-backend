package app.streat.backend.cart.domain.models

import org.springframework.data.mongodb.core.mapping.Field

data class CartItem(
    @Field("shop_id")
    val shopId: String,
    @Field("item_name")
    val itemName: String,
    var quantity: Int = 0,
    val price: Double
)