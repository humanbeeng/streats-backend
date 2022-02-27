package com.streats.backendphaseone.shop.sevices

import com.streats.backendphaseone.shop.data.repositories.StreatsShopRepository
import com.streats.backendphaseone.shop.domain.models.DishItem
import com.streats.backendphaseone.shop.domain.models.StreatsShop
import com.streats.backendphaseone.shop.models.LocationEntity
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

@Configuration
class ShopConfig {
    @Bean
    fun init(shopRepository: StreatsShopRepository): CommandLineRunner {
        return CommandLineRunner {
//            print("Hello There")
            val shop = StreatsShop(
                id = null,
                shop_name = "Chethan Centre",
                is_shop_open = false,
                is_takeaway_supported = true,
                shop_items = mutableListOf(
//                    DishItem(id = , name = "MasalaPuri", price = 30),
//                    DishItem(id = , name = "PaniPuri", price = 25),
//                    DishItem(id = , name = "Gobi", price = 40),
//                    DishItem(id = , name = "Bhel", price = 25),
//                    DishItem(id = , name = "Fried Rice", price = 50),
//                    DishItem(id = ,  name = "Egg Rice", price = 40),
                ),
                location = LocationEntity(
                    GeoJsonPoint(
                        100.4, 98.3
                    ),
                    name = "Ashok nagar"
                ),
                shop_owner_phone_number = "8904756775"
            )
            shopRepository.save(shop)


//            val dishItem()
        }

    }
}