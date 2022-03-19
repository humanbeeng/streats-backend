package app.streat.backend.shop.domain.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class DishItem(
    @Id
    val id: ObjectId? = null,
    val name: String,
    val price: Int,
)
