package net.ccbluex.liquidbounce.api.oauth

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.ccbluex.liquidbounce.config.Configurable
import net.ccbluex.liquidbounce.config.util.Exclude
import net.ccbluex.liquidbounce.features.cosmetic.Cosmetic
import java.util.UUID

// 管理客户端账户的单例对象，继承自Configurable类
object ClientAccountManager : Configurable("account") {
    var clientAccount by value("account", ClientAccount.EMPTY_ACCOUNT)
}

/**
 * 表示用于与LiquidBounce API进行身份验证的客户端账户。
 * 可能包含从API获取的额外信息。
 */
data class ClientAccount(
    private var session: OAuthSession? = null,
    @Exclude
    var userInformation: UserInformation? = null,
    @Exclude
    var cosmetics: Set<Cosmetic>? = null
) {

    // 获取当前会话，如果会话已过期则更新会话
    private suspend fun takeSession(): OAuthSession = session?.takeIf { !it.accessToken.isExpired() } ?: run {
        renew()
        session ?: error("No session")
    }

    // 更新用户信息
    suspend fun updateInfo(): Unit = withContext(Dispatchers.IO) {
        val info = OAuthClient.getUserInformation(takeSession())
        userInformation = info
    }

    // 更新用户的化妆品信息
    suspend fun updateCosmetics(): Unit = withContext(Dispatchers.IO) {
        cosmetics = OAuthClient.getCosmetics(takeSession())
    }

    // 转移临时所有权
    suspend fun transferTemporaryOwnership(uuid: UUID): Unit = withContext(Dispatchers.IO) {
        OAuthClient.transferTemporaryOwnership(takeSession(), uuid)
    }

    // 更新会话
    suspend fun renew() = withContext(Dispatchers.IO) {
        session = OAuthClient.renewToken(takeSession())
    }

    companion object {
        // 空账户实例
        val EMPTY_ACCOUNT = ClientAccount(null, null, null)
    }

}

// 用户信息数据类
data class UserInformation(
    @SerializedName("user_id") val userId: String,
    val premium: Boolean
)