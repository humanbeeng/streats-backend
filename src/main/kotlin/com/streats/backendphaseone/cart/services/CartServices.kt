package com.streats.backendphaseone.cart.services

import com.streats.backendphaseone.auth.service.StreatsUserService
import com.streats.backendphaseone.cart.domain.usecase.UpdateUserCart
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service


@Component
class CartServices(
    private val streatsUserService: StreatsUserService,
    private val updateUserCart: UpdateUserCart
) {

    fun addToCart(id: String, dishId: String, quantity: Int) {
        val user = streatsUserService.getStreatsCustomer(id)

        updateUserCart.userCart(user, dishId, quantity)
    }
}