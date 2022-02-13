package com.streats.backendphaseone.auth.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.streats.backendphaseone.auth.data.dto.AuthResponse
import com.streats.backendphaseone.auth.domain.models.StreatsCustomer
import com.streats.backendphaseone.auth.service.StreatsUserService
import com.streats.backendphaseone.auth.util.JWTUtil
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
            roles = listOf("USER")
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
            roles = listOf("ADMIN")
        )
    }


}