package app.streat.backend.core.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JWTConfig {

    @Value("\${JWT_SIGNING_KEY}")
    lateinit var signingKey: String
}