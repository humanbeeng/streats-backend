package app.streat.backend.auth.data.repository

import app.streat.backend.auth.domain.models.user.StreatsCustomer
import org.springframework.data.mongodb.repository.MongoRepository

interface AuthRepository : MongoRepository<StreatsCustomer, String> {
    fun findStreatsCustomerByFirebaseUID(firebaseUID: String): StreatsCustomer

    fun existsStreatsCustomerByFirebaseUID(firebaseUID: String): Boolean
}