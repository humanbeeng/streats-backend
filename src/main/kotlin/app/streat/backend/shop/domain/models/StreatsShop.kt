package app.streat.backend.shop.domain.models

import app.streat.backend.core.util.CoreConstants.EMPTY
import app.streat.backend.order.domain.model.order.Order
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "shops")
data class StreatsShop(
    @Id
    val shopId: String? = null,

    val shopName: String,

    val vendorId: String = EMPTY,

    val shopOwnerPhoneNumber: String,

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    val location: GeoJsonPoint,

    val zipcode: String,


    val shopItems: MutableMap<String, DishItem>,

    val isTakeawaySupported: Boolean,

    var isShopOpen: Boolean,

    val featured: Boolean = false,

    val ongoingOrders: MutableList<Order> = emptyList<Order>().toMutableList()
)

