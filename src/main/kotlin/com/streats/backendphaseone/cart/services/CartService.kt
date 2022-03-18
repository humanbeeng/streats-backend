package com.streats.backendphaseone.cart.services

import com.streats.backendphaseone.auth.service.StreatsUserService
import org.springframework.stereotype.Service


@Service
class CartService(
    private val streatsUserService: StreatsUserService,
) {


}