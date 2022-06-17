package app.streat.backend.home.controller

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.core.util.JWTUtil
import app.streat.backend.home.domain.models.Home
import app.streat.backend.shop.domain.models.StreatsShop
import app.streat.backend.shop.services.ShopService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/home")
class HomeController(
    private val shopService: ShopService,
    private val jwtUtil: JWTUtil,
    private val userService: StreatsUserService
) {

    /**
     * TODO : Replace with nearby function and intake co-ordinates from client
     */
    @GetMapping
    fun home(@RequestHeader("Authorization") accessToken: String): ResponseEntity<Home> {
        return try {
            val userId = jwtUtil.getId(accessToken)
            val username = userService.getStreatsCustomerById(userId).username

            val featuredShops = shopService.getFeaturedShops()
            val shops = shopService.getAllShops()
            val home =
                Home(username, featuredShops, shops)
            ResponseEntity.ok(home)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{shopId}")
    fun shop(@PathVariable shopId: String): ResponseEntity<StreatsShop> {
        return try {
            val shop = shopService.getShopByShopId(shopId)
            ResponseEntity.ok(shop)
        } catch (e: NoSuchElementException) {
            ResponseEntity.badRequest().build()
        }

    }
}