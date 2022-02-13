package com.streats.backendphaseone.auth.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class StreatsCustomer(
    @Id
    val firebaseUID: String,

    override val email: String,
    override val username: String,

    val profilePictureUrl: String,
    val roles: List<String>

) : User