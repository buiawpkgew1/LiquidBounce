package net.ccbluex.liquidbounce.api.oauth

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.*
import kotlinx.coroutines.*
import net.ccbluex.liquidbounce.api.ClientApi.API_V3_ENDPOINT
import net.ccbluex.liquidbounce.config.util.decode
import net.ccbluex.liquidbounce.features.cosmetic.Cosmetic
import net.ccbluex.liquidbounce.utils.io.HttpClient
import java.net.InetSocketAddress
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// OAuthClient 对象，用于处理OAuth认证流程
object OAuthClient {

    private const val CLIENT_ID = "J2hzqzCxch8hfOPRFNINOZV5Ma4X4BFdZpMjAVEW"
    private const val AUTHORIZE_URL = "https://auth.liquidbounce.net/application/o/authorize/"
    private const val TOKEN_URL = "https://auth.liquidbounce.net/application/o/token/"

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var serverPort: Int? = null
    @Volatile
    private var authCodeContinuation: Continuation<String>? = null

    // 在指定的协程作用域中运行给定的代码块
    fun runWithScope(block: suspend CoroutineScope.() -> Unit) {
        scope.launch { block() }
    }

    // 启动OAuth认证流程，返回客户端账户信息
    suspend fun startAuth(onUrl: (String) -> Unit): ClientAccount {
        val (codeVerifier, codeChallenge) = PKCEUtils.generatePKCE()
        val state = UUID.randomUUID().toString()

        if (serverPort == null) {
            serverPort = startNettyServer()
        }

        val redirectUri = "http://127.0.0.1:$serverPort/"
        val authUrl = buildAuthUrl(codeChallenge, state, redirectUri)

        onUrl(authUrl)
        val code = waitForAuthCode()
        val tokenResponse = exchangeCodeForTokens(code, codeVerifier, redirectUri)

        serverPort = null

        return ClientAccount(session = tokenResponse.toAuthSession())
    }

    // 启动Netty服务器并返回绑定的端口号
    private suspend fun startNettyServer(): Int = suspendCoroutine { cont ->
        scope.launch {
            runCatching {
                val bossGroup = NioEventLoopGroup(1)
                val workerGroup = NioEventLoopGroup()

                try {
                    val bootstrap = ServerBootstrap()
                    bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel::class.java)
                        .childHandler(NettyChannelInitializer())

                    val channelFuture: ChannelFuture = bootstrap.bind(0).sync()
                    val localPort = (channelFuture.channel().localAddress() as InetSocketAddress).port
                    cont.resume(localPort)

                    // Keep server running until closed
                    channelFuture.channel().closeFuture().sync()
                } finally {
                    bossGroup.shutdownGracefully()
                    workerGroup.shutdownGracefully()
                }
            }.onFailure { e -> cont.resumeWithException(e) }
        }
    }

    // Netty通道初始化器，用于配置通道处理程序
    class NettyChannelInitializer : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
            ch.pipeline().addLast(HttpServerCodec(), HttpObjectAggregator(65536), NettyAuthHandler())
        }
    }

    // Netty认证处理程序，处理HTTP请求并获取认证码
    class NettyAuthHandler : SimpleChannelInboundHandler<FullHttpRequest>() {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest) {
            val uri = msg.uri()
            val uriData = QueryStringDecoder(uri)
            val queryParameters = uriData.parameters().mapValues { it.value[0] }
            val code = queryParameters["code"]

            authCodeContinuation?.let {
                if (code != null) {
                    val response = DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.OK
                    ).apply {
                        content().writeBytes(OAuthClient.SUCCESS_HTML.toByteArray())
                        headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
                        headers().set(HttpHeaderNames.CONTENT_LENGTH, content().readableBytes())
                    }
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
                    it.resume(code)
                } else {
                    it.resumeWithException(Exception("No code found in the redirect URL"))
                }
                authCodeContinuation = null
            }
        }
    }

    // 构建认证URL
    private inline fun buildAuthUrl(codeChallenge: String, state: String, redirectUri: String): String {
        return "$AUTHORIZE_URL?client_id=$CLIENT_ID&redirect_uri=$redirectUri&response_type=code&state=$state" +
            "&code_challenge=$codeChallenge&code_challenge_method=S256"
    }

    // 等待认证码
    private suspend fun waitForAuthCode(): String = suspendCoroutine { cont ->
        authCodeContinuation = cont
    }

    // 交换认证码以获取令牌
    private suspend fun exchangeCodeForTokens(code: String, codeVerifier: String,
                                              redirectUri: String): TokenResponse = withContext(Dispatchers.IO) {
        val response = HttpClient.postForm(
            TOKEN_URL,
            "client_id=$CLIENT_ID&code=$code&code_verifier=$codeVerifier&grant_type=authorization_code" +
                "&redirect_uri=$redirectUri"
        )
        return@withContext decode(response)
    }

    // 刷新令牌
    suspend fun renewToken(session: OAuthSession): OAuthSession = withContext(Dispatchers.IO) {
        val response = HttpClient.postForm(
            TOKEN_URL,
            "client_id=$CLIENT_ID&refresh_token=${session.refreshToken}&grant_type=refresh_token"
        )

        val tokenResponse = decode<TokenResponse>(response)
        return@withContext tokenResponse.toAuthSession()
    }

    // 获取用户信息
    suspend fun getUserInformation(session: OAuthSession): UserInformation = withContext(Dispatchers.IO) {
        val response = HttpClient.request("$API_V3_ENDPOINT/oauth/user", "GET", headers =
            arrayOf("Authorization" to "Bearer ${session.accessToken}")
        )
        return@withContext decode(response)
    }

    // 获取用户的化妆品信息
    suspend fun getCosmetics(session: OAuthSession): Set<Cosmetic> = withContext(Dispatchers.IO) {
        val response = HttpClient.request("$API_V3_ENDPOINT/cosmetics/self", "GET", headers =
            arrayOf("Authorization" to "Bearer ${session.accessToken}")
        )
        return@withContext decode(response)
    }

    // 转移临时所有权
    suspend fun transferTemporaryOwnership(session: OAuthSession, uuid: UUID) = withContext(Dispatchers.IO) {
        HttpClient.request("$API_V3_ENDPOINT/cosmetics/self", "PUT", headers =
            arrayOf(
                "Authorization" to "Bearer ${session.accessToken}",
                "Content-Type" to "application/json"
            ),
            inputData = JsonObject().apply {
                addProperty("uuid", uuid.toString())
            }.toString().toByteArray()
        )
    }

    // 令牌响应数据类
    data class TokenResponse(
        @SerializedName("access_token") val accessToken: String,
        // In seconds
        @SerializedName("expires_in") val expiresIn: Long,
        @SerializedName("refresh_token") val refreshToken: String?
    ) {
        fun toAuthSession(): OAuthSession {
            val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
            return OAuthSession(
                accessToken = ExpiryValue(accessToken, expiresAt),
                refreshToken = refreshToken ?: throw NullPointerException("Refresh token is null")
            )
        }
    }

    private const val SUCCESS_HTML = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Authentication Successful</title>
            <style>
                body { font-family: Arial, sans-serif; background-color: #121212; color: #ffffff; text-align: center; padding: 50px; }
                .container { background-color: #1E1E1E; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.5); display: inline-block; }
                h1 { color: #4CAF50; }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>Authentication Successful</h1>
                <p>You have successfully authenticated. You can close this tab now.</p>
            </div>
        </body>
        </html>
    """
}