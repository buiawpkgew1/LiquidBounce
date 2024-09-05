package net.ccbluex.liquidbounce.api.oauth

/**
 * 包含访问令牌和刷新令牌的数据类。
 */
data class OAuthSession(
    var accessToken: ExpiryValue<String>,
    val refreshToken: String,
)