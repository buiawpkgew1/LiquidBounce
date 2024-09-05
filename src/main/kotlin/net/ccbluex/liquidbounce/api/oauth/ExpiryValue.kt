package net.ccbluex.liquidbounce.api.oauth

/**
 * 表示一个在特定时间 [expiresAt] 过期的值。
 */
data class ExpiryValue<T>(val value: T, val expiresAt: Long) {
    /**
     * 检查值是否已过期。
     */
    fun isExpired() = expiresAt < System.currentTimeMillis()

    /**
     * 返回值的字符串表示形式。
     */
    override fun toString() = value.toString()
}