package com.streats.backendphaseone.shop.models

import com.mongodb.client.model.geojson.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

class LocationEntity(
    val geometry: GeoJsonPoint,
    val name: String
) {
}

