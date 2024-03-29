package app.streat.backend.core.util

import app.streat.backend.auth.domain.models.user.StreatsCustomer
import app.streat.backend.auth.utils.AuthConstants.BEARER_PREFIX
import app.streat.backend.auth.utils.AuthConstants.BEARER_STRING_LENGTH
import app.streat.backend.auth.utils.AuthConstants.JWT_ISSUER
import app.streat.backend.auth.utils.AuthConstants.PARAM_ROLES
import app.streat.backend.core.config.JWTConfig
import app.streat.backend.vendor.domain.models.streats_vendor.StreatsVendor
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component

/**
 * Note : TODO : Replace FirebaseUID with appropriate names
 */
@Component
class JWTUtil(jwtConfig: JWTConfig) {


    val jwtSigningKey: String = jwtConfig.signingKey

    // TODO : Add expiry date to three month
    fun createAccessToken(streatsCustomer: StreatsCustomer): String {
        val roles: MutableList<String> = mutableListOf()

        streatsCustomer.roles.forEach { role -> roles.add(role) }

        return JWT.create()
            .withIssuer(JWT_ISSUER)
            .withSubject(streatsCustomer.firebaseUID)
            .withClaim(PARAM_ROLES, roles)
            .sign(Algorithm.HMAC256(jwtSigningKey))
    }

    fun createVendorAccessToken(streatsVendor: StreatsVendor): String {
        val roles: MutableList<String> = mutableListOf()

        streatsVendor.roles.forEach { role -> roles.add(role) }

        return JWT.create()
            .withIssuer(JWT_ISSUER)
            .withSubject(streatsVendor.vendorId)
            .withClaim(PARAM_ROLES, roles)
            .sign(Algorithm.HMAC256(jwtSigningKey))
    }

    fun verifyAccessToken(accessToken: String): Boolean {
        val verifier = JWT.require(Algorithm.HMAC256(jwtSigningKey)).build()

        return try {
            verifier.verify(accessToken)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getDecodedToken(accessToken: String): DecodedJWT {
        val verifier = JWT.require(Algorithm.HMAC256(jwtSigningKey)).build()
        return verifier.verify(accessToken)
    }

    /**
     * TODO : Move this to UserService and refactor to get username from UserService
     */
    fun getUsername(authorizationHeader: String): String {
        return if (isAuthorizationHeaderValid(authorizationHeader)) {
            val incomingAccessToken = authorizationHeader.substring(BEARER_STRING_LENGTH)


            val decodedToken = getDecodedToken(incomingAccessToken)


            decodedToken.subject
        } else {
            throw BadCredentialsException("Invalid access token")
        }
    }

    fun getId(authorizationHeader: String): String {
        return if (isAuthorizationHeaderValid(authorizationHeader)) {
            val incomingAccessToken = authorizationHeader.substring(BEARER_STRING_LENGTH)


            val decodedToken = getDecodedToken(incomingAccessToken)


            decodedToken.subject
        } else {
            throw BadCredentialsException("Invalid access token")
        }
    }

    private fun isAuthorizationHeaderValid(authorizationHeader: String): Boolean {
        return authorizationHeader.isNotBlank() && authorizationHeader.startsWith(BEARER_PREFIX)
    }


}