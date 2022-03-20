package app.streat.backend.auth.util

import app.streat.backend.auth.domain.usecase.models.StreatsCustomer
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Service

@Service
class JWTUtil {

    private val signingKey: String = "signing-key"

    fun createAccessToken(streatsCustomer: StreatsCustomer): String {
        val roles: MutableList<String> = mutableListOf()

        streatsCustomer.roles.forEach { role -> roles.add(role) }

        return JWT.create()
            .withIssuer("Streats")
            .withSubject(streatsCustomer.username)
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

    fun getUsername(authorizationHeader: String): String {
        return if (authorizationHeader.isNotBlank() && authorizationHeader.startsWith("Bearer ")) {
            val incomingAccessToken = authorizationHeader.substring("Bearer ".length)


            val decodedToken = getDecodedToken(incomingAccessToken)


            decodedToken.subject
        } else {
            ""
        }
    }


}