package app.streat.backend.shop.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "shops")
data class StreatsShop(
    @Id
    val id: String? = null,

    @Field("shop_name")
    val shopName: String,

    @Field("shop_owner_phone_number")
    val shopOwnerPhoneNumber: String,

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    val location: GeoJsonPoint,

    val zipcode: String,


    @Field("shop_items")
    val shopItems: List<DishItem>,

    @Field("is_takeaway_supported")
    val isTakeawaySupported: Boolean,

    @Field("is_shop_open")
    val isShopOpen: Boolean,

    val featured: Boolean = false
)

