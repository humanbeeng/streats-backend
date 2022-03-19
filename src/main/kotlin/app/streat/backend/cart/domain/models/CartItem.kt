package app.streat.backend.cart.domain.models

import org.bson.types.ObjectId

data class CartItem(var quantity: Int, val id: ObjectId, val name: String, val price: Int)