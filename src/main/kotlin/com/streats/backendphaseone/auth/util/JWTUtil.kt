package com.streats.backendphaseone.auth.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.streats.backendphaseone.auth.domain.models.StreatsCustomer
import org.springframework.stereotype.Service

@Service
class JWTUtil {

    private val signingKey: String = "signing-key"

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


}