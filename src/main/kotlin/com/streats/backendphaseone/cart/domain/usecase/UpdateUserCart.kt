package com.streats.backendphaseone.cart.domain.usecase

import com.streats.backendphaseone.auth.domain.usecase.models.StreatsCustomer
import com.streats.backendphaseone.auth.service.StreatsUserService
import com.streats.backendphaseone.cart.domain.models.CartItem
import com.streats.backendphaseone.cart.services.CartServices
import com.streats.backendphaseone.shop.sevices.ShopService
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class UpdateUserCart(
//    private val cartServices: CartServices,
    private val shopService: ShopService,
    private val streatsUserService: StreatsUserService
) {


    fun userCart(streatsCustomer: StreatsCustomer, dishID: ObjectId, quantity: Int) {
        val doesItemExist = streatsCustomer.cart.containsKey(dishID)
        if (doesItemExist) {
            updateCart(streatsCustomer, dishID, quantity)
        } else {
            insertToCart(streatsCustomer, dishID, quantity)
        }
    }

    private fun insertToCart(streatsCustomer: StreatsCustomer, dishID: ObjectId, quantity: Int) {
        val shop = shopService.getShopByDishId(dishID)
        val id = dishID.toString()
        val dishItem = shop.get().shop_items.find { item ->
            item?.id == dishID
        }
        val cartItem = dishItem?.let {
            CartItem(
                id = dishItem.id,
                price = dishItem.price,
                name = dishItem.name,
                quantity = quantity
            )
        }
        if (cartItem != null) {
            streatsCustomer.cart[dishID] = cartItem
        }
        streatsUserService.updateStreatsCustomer(streatsCustomer)
    }

    private fun updateCart(streatsCustomer: StreatsCustomer, dishID: ObjectId, quantity: Int) {
        streatsCustomer.cart[dishID]?.quantity = streatsCustomer.cart[dishID]?.quantity?.plus(quantity)!!
        streatsUserService.updateStreatsCustomer(streatsCustomer)
    }
}