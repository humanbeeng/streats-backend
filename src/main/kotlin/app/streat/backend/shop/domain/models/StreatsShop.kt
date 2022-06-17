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
    @Id val shopId: String? = null,

//    TODO : Change it to a generic image of a shop
    val shopImage: String = EMPTY,

    val shopName: String,

    val vendorId: String = EMPTY,

    val shopOwnerPhoneNumber: String,

//  Note: Geospatial queries will not work out of the box when GeoJsonPoints are nested
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE) val coordinates: GeoJsonPoint,

    val zipCode: String,

    val locationName: String,

    val shopItems: MutableMap<String, DishItem> = mutableMapOf(),

    val isTakeawaySupported: Boolean = false,

    var isShopOpen: Boolean = false,

    val featured: Boolean = false,

    val ongoingOrders: MutableList<Order> = mutableListOf()
)

