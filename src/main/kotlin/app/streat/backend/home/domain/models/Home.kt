package app.streat.backend.home.domain.models

import app.streat.backend.shop.domain.models.StreatsShop


data class Home(
    val username: String,
    val featuredShops: List<StreatsShop>,
    val nearbyShops: List<StreatsShop>
)
