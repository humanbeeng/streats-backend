package app.streat.backend.shop.domain.models

import org.springframework.data.mongodb.core.mapping.Field

data class DishItem(

    @Field("dish_name")
    val dishName: String,
    val price: Double,
)
