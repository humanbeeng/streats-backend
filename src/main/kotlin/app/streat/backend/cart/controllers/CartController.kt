package app.streat.backend.cart.controllers

import app.streat.backend.cart.services.CartService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/cart"])
class CartController(private val cartService: CartService) {


}