package com.streats.backendphaseone.shop.controllers

import com.streats.backendphaseone.shop.domain.models.StreatsShop
import com.streats.backendphaseone.shop.sevices.ShopService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/home"])
class ShopController(private val shopService: ShopService) {

    @GetMapping(path = ["/shops"])
    fun getShops(): ResponseEntity<List<StreatsShop>> {
        return ResponseEntity.ok(shopService.getShops())
    }

    @GetMapping(path = ["/search"])
    fun getShop(@RequestParam shop_name: String): ResponseEntity<List<StreatsShop>> {
        return ResponseEntity.ok(shopService.getShop(shop_name))
    }
}