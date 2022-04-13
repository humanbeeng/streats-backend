package app.streat.backend.core.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties(prefix = "cashfree")
class CashfreeConfig{

    @Value("\${CASHFREE_CLIENT_ID}")
    lateinit var clientId : String

    @Value("\${CASHFREE_CLIENT_SECRET}")
    lateinit var clientSecret : String

    @Value("\${CASHFREE_TOKEN_URL}")
    lateinit var tokenUrl : String


}