package app.streat.backend.core.util

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.springframework.stereotype.Service

@Service
class Hmac256Util {

    fun createBase64EncodedSignature(data: String, key: String): String {

        val hmac256 = HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)

        val hash = hmac256.hmac(data)

        return Base64.encodeBase64String(hash).toString()
    }
}