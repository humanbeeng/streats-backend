package app.streat.backend.auth.domain.usecase

import app.streat.backend.auth.data.dto.AuthResponse
import app.streat.backend.auth.domain.usecase.models.StreatsCustomer
import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.core.util.JWTUtil
import app.streat.backend.cart.domain.models.Cart
import com.google.firebase.auth.FirebaseAuth
import org.springframework.stereotype.Service


@Service
class AuthenticateUser(private val service: StreatsUserService, private val jwtUtil: JWTUtil) {


    fun authenticate(idToken: String): AuthResponse {

        return if (verifyUser(idToken)) {

            val streatsCustomer = createStreatsCustomerFromFirebaseToken(idToken)

            if (checkUserExists(streatsCustomer.firebaseUID).not()) {
                service.createStreatsCustomer(streatsCustomer)
            }

            val accessToken = jwtUtil.createAccessToken(streatsCustomer)

            AuthResponse(accessToken = accessToken, isVerified = true)

        } else
            AuthResponse(accessToken = "", false)

    }


    private fun verifyUser(idToken: String): Boolean {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(idToken)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun createStreatsCustomerFromFirebaseToken(idToken: String): StreatsCustomer {
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
        val uid: String = decodedToken.uid
        val name: String = decodedToken.name
        val email: String = decodedToken.email
        val profilePictureUrl = decodedToken.picture
        return StreatsCustomer(
            username = name,
            email = email,
            firebaseUID = uid,
            profilePictureUrl = profilePictureUrl,
            roles = listOf("USER"),
            cart = Cart(),
            orders = mutableListOf()
        )
    }

    private fun checkUserExists(uid: String): Boolean {
        return service.checkUserExists(uid)
    }

    /**
     * Delete this method, Using for testing -> Creating admin user
     * Not using Firebase Server Side Verification
     */

    fun createAdminUser(idToken: String): AuthResponse {

        val streatsAdmin = createStreatsAdminUserFromFirebaseToken(idToken)
        val accessToken = jwtUtil.createAccessToken(streatsAdmin)

        service.updateStreatsCustomer(streatsAdmin)
        return AuthResponse(isVerified = true, accessToken = accessToken)
    }


    private fun createStreatsAdminUserFromFirebaseToken(idToken: String): StreatsCustomer {
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
        val uid: String = decodedToken.uid
        val name: String = decodedToken.name
        val email: String = decodedToken.email
        val profilePictureUrl = decodedToken.picture
        return StreatsCustomer(
            username = name,
            email = email,
            firebaseUID = uid,
            profilePictureUrl = profilePictureUrl,
            roles = listOf("ADMIN"),
            cart = Cart(),
            orders = mutableListOf()
        )
    }


}