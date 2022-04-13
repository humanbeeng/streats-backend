package app.streat.backend.core.util

import app.streat.backend.auth.domain.usecase.models.StreatsCustomer
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

@Service
class JWTUtil {


    @Value("\${JWT_SIGNING_KEY}")
    private lateinit var signingKey: String

    fun createAccessToken(streatsCustomer: StreatsCustomer): String {
        val roles: MutableList<String> = mutableListOf()

        streatsCustomer.roles.forEach { role -> roles.add(role) }

        return JWT.create()
            .withIssuer("Streats")
            .withSubject(streatsCustomer.firebaseUID)
            .withClaim("roles", roles)
            .sign(Algorithm.HMAC256(signingKey))
    }


    fun verifyAccessToken(accessToken: String): Boolean {
        val verifier = JWT.require(Algorithm.HMAC256(signingKey)).build()

        return try {
            verifier.verify(accessToken)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getDecodedToken(accessToken: String): DecodedJWT {
        val verifier = JWT.require(Algorithm.HMAC256(signingKey)).build()
        return verifier.verify(accessToken)
    }

    /**
     * TODO : Move this to UserService and refactor to get username from UserService
     */
    fun getUsername(authorizationHeader: String): String {
        return if (isAuthorizationHeaderValid(authorizationHeader)) {
            val incomingAccessToken = authorizationHeader.substring("Bearer ".length)


            val decodedToken = getDecodedToken(incomingAccessToken)


            decodedToken.subject
        } else {
            throw BadCredentialsException("Invalid access token")
        }
    }

    fun getUserId(authorizationHeader: String): String {
        return if (isAuthorizationHeaderValid(authorizationHeader)) {
            val incomingAccessToken = authorizationHeader.substring("Bearer ".length)


            val decodedToken = getDecodedToken(incomingAccessToken)


            decodedToken.subject
        } else {
            throw BadCredentialsException("Invalid access token")
        }
    }

    private fun isAuthorizationHeaderValid(authorizationHeader: String): Boolean {
        return authorizationHeader.isNotBlank() && authorizationHeader.startsWith("Bearer ")
    }


}