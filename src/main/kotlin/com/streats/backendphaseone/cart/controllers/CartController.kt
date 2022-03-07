package com.streats.backendphaseone.cart.controllers

import com.streats.backendphaseone.cart.data.dto.CartRequestBody
import com.streats.backendphaseone.cart.services.CartServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/cart"])
class CartController(private val cartService: CartServices) {

    @GetMapping(path = ["/update"])
    fun addItemToCart(@RequestBody requestBody: CartRequestBody): ResponseEntity<HttpStatus> {
        cartService.addToCart(requestBody.id, requestBody.dish_id, requestBody.quantity)



        return ResponseEntity.ok(HttpStatus.ACCEPTED)
    }

}