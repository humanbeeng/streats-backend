package com.streats.backendphaseone.shop.controllers

import com.streats.backendphaseone.shop.domain.models.StreatsShop
import com.streats.backendphaseone.shop.sevices.ShopService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/shop"])
class ShopController(private val shopService: ShopService) {

    @GetMapping
    fun getShops(): ResponseEntity<List<StreatsShop>> {
        return ResponseEntity.ok(shopService.getAllShops())
    }


    /**
     * TODO : Move this API to Admin Service
     */
    @PostMapping("/add/test")
    fun addNewDummyShop(): StreatsShop {
        return shopService.createNewDummyShop()
    }


}