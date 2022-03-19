package app.streat.backend.auth.service

import app.streat.backend.auth.data.repository.AuthRepository
import app.streat.backend.auth.domain.usecase.models.StreatsCustomer
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class StreatsUserService(private val repo: AuthRepository) : UserDetailsService {

    override fun loadUserByUsername(firebaseId: String): UserDetails {
        val streatsUser = repo.findStreatsCustomerByFirebaseUID(firebaseId)

        return User
            .withUsername(streatsUser.firebaseUID)
            .authorities(streatsUser.roles.map { SimpleGrantedAuthority(it) }.toMutableList())
            .build()

    }

    fun createStreatsCustomer(streatsCustomer: StreatsCustomer): StreatsCustomer {
        return repo.save(streatsCustomer)
    }

    fun updateStreatsCustomer(streatsCustomer: StreatsCustomer): StreatsCustomer {
        return repo.save(streatsCustomer)
    }

    fun getStreatsCustomer(id: String): StreatsCustomer {
        return repo.findStreatsCustomerByFirebaseUID(id)
    }

    fun checkUserExists(id: String): Boolean {
        return repo.existsStreatsCustomerByFirebaseUID(id)
    }

}