package com.streats.backendphaseone.auth.data.repository

import com.streats.backendphaseone.auth.domain.models.StreatsCustomer
import org.springframework.data.mongodb.repository.MongoRepository

interface AuthRepository : MongoRepository<StreatsCustomer, String> {
    fun findStreatsCustomerByFirebaseUID(firebaseUID: String): StreatsCustomer

    fun existsStreatsCustomerByFirebaseUID(firebaseUID: String): Boolean
}