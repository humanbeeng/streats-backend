package com.streats.backendphaseone.shop.domain.models

import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class LocationEntity(
    val geometry: GeoJsonPoint,
    val locationName: String
)


