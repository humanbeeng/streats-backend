package com.streats.backendphaseone.shop.controllers

import com.streats.backendphaseone.shop.data.dto.DishItemDTO
import com.streats.backendphaseone.shop.domain.models.StreatsShop
import com.streats.backendphaseone.shop.sevices.ShopService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/home"])
class ShopController(private val shopService: ShopService) {

    @GetMapping(path = ["/shops"])
    fun getShops(): ResponseEntity<List<StreatsShop>> {
        return ResponseEntity.ok(shopService.getShops())
    }

    @PostMapping(path = ["/shops/add"])
    fun addItem(itemDTO: DishItemDTO): ResponseEntity<HttpStatus> {
        shopService.addItem(itemDTO)
        return ResponseEntity.ok(HttpStatus.ACCEPTED)
    }


    @GetMapping(path = ["/search"])
    fun getShop(@RequestParam shop_name: String): ResponseEntity<List<StreatsShop>> {
        return ResponseEntity.ok(shopService.getShop(shop_name))
    }
}