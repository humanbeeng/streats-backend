package app.streat.backend.auth.domain.usecase

import app.streat.backend.auth.data.dto.LoginResponseDTO
import app.streat.backend.auth.domain.models.auth_request.AuthRequest
import app.streat.backend.auth.domain.models.login_request.LoginRequest
import app.streat.backend.auth.domain.models.user.StreatsCustomer
import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.auth.service.exceptions.AuthException
import app.streat.backend.cart.domain.models.Cart
import app.streat.backend.core.util.Constants.EMPTY
import app.streat.backend.core.util.JWTUtil
import com.google.firebase.auth.FirebaseAuth
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Service

/**
 * TODO : Refactor this service by breaking out to different use cases
 *
 * 1. Authenticate
 *
 * 2. Login
 */

@Service
class AuthenticateUser(
    private val userService: StreatsUserService,
    private val jwtUtil: JWTUtil
) {


    fun authenticate(authRequest: AuthRequest) {

        val userId = jwtUtil.getUserId(authRequest.accessToken)
        if (checkUserExists(userId)) {
            val user = userService.getStreatsCustomer(userId)

            user.currentLocation = authRequest.currentLocation
            user.fcmTokenOfCurrentLoggedInDevice = authRequest.fcmToken

            userService.updateStreatsCustomer(user)
        } else throw AuthException.UserNotFoundException("User not found")

    }


    fun login(loginRequest: LoginRequest): LoginResponseDTO {

        return if (verifyUser(loginRequest.idToken)) {

            val streatsCustomer = createStreatsCustomerFromFirebaseToken(loginRequest)

            if (checkUserExists(streatsCustomer.firebaseUID).not()) {
                userService.createStreatsCustomer(streatsCustomer)
            } else {
                userService.updateStreatsCustomer(streatsCustomer)
            }

            val accessToken = jwtUtil.createAccessToken(streatsCustomer)

            LoginResponseDTO(accessToken = accessToken, isVerified = true)

        } else
            LoginResponseDTO(accessToken = EMPTY, false)

    }


    private fun verifyUser(idToken: String): Boolean {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(idToken)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun createStreatsCustomerFromFirebaseToken(loginRequest: LoginRequest): StreatsCustomer {
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(loginRequest.idToken)
        val uid: String = decodedToken.uid
        val name: String = decodedToken.name
        val email: String = decodedToken.email
        val profilePictureUrl = decodedToken.picture
        val currentLocation: GeoJsonPoint = loginRequest.currentLocation
        val fcmTokenOfCurrentLoggedInDevice: String = loginRequest.fcmToken
        return StreatsCustomer(
            username = name,
            email = email,
            firebaseUID = uid,
            profilePictureUrl = profilePictureUrl,
            roles = listOf("USER"),
            cart = Cart(),
            orders = mutableListOf(),
            fcmTokenOfCurrentLoggedInDevice = fcmTokenOfCurrentLoggedInDevice,
            currentLocation = currentLocation
        )
    }

    private fun checkUserExists(userId: String): Boolean {
        return userService.checkUserExists(userId)
    }

    /**
     * Delete this method, Using for testing -> Creating admin user
     * Not using Firebase Server Side Verification
     */


    fun createAdminUser(loginRequest: LoginRequest): LoginResponseDTO {

        val streatsAdmin = createStreatsAdminUserFromFirebaseToken(loginRequest)
        val accessToken = jwtUtil.createAccessToken(streatsAdmin)

        userService.updateStreatsCustomer(streatsAdmin)
        return LoginResponseDTO(isVerified = true, accessToken = accessToken)
    }


    private fun createStreatsAdminUserFromFirebaseToken(loginRequest: LoginRequest): StreatsCustomer {
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(loginRequest.idToken)
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
            orders = mutableListOf(),
            currentLocation = loginRequest.currentLocation,
            fcmTokenOfCurrentLoggedInDevice = loginRequest.fcmToken
        )
    }


}