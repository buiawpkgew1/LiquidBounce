package net.ccbluex.liquidbounce.api.oauth

import java.security.MessageDigest
import java.util.*

// PKCEUtils 对象用于生成 PKCE（Proof Key for Code Exchange）所需的 codeVerifier 和 codeChallenge
object PKCEUtils {
    // generatePKCE 方法生成一对 codeVerifier 和 codeChallenge
    fun generatePKCE(): Pair<String, String> {
        val codeVerifier = UUID.randomUUID().toString().replace("-", "")
        val codeChallenge = Base64.getEncoder().encodeToString(
            MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray())
        ).replace("=", "").replace("+", "-").replace("/", "_")
        return codeVerifier to codeChallenge
    }
}