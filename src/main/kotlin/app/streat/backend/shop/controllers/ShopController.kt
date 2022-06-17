package app.streat.backend.shop.controllers

import app.streat.backend.shop.data.repositories.StreatsShopRepository
import app.streat.backend.shop.domain.models.StreatsShop
import app.streat.backend.shop.services.ShopService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * TODO : Migrate to AdminService
 */
@RestController
@RequestMapping("/shop")
class ShopController(private val shopService: ShopService, private val repo: StreatsShopRepository) {

    @GetMapping("/delete/all")
    fun deleteAll() {
        return shopService.deleteAllShops()
    }

    @GetMapping("/test/add")
    fun addDummyShops(): List<StreatsShop> {
        return shopService.addDummyShops()
    }

    @GetMapping
    fun allShops(): List<StreatsShop> {
        return shopService.getAllShops()
    }


}