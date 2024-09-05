/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.lang

import net.ccbluex.liquidbounce.config.Configurable
import net.ccbluex.liquidbounce.config.util.decode
import net.ccbluex.liquidbounce.utils.client.logger
import net.ccbluex.liquidbounce.utils.client.mc
import net.minecraft.text.*
import net.minecraft.util.Language
import java.util.*

// 根据给定的键和参数生成可变的文本对象
fun translation(key: String, vararg args: Any): MutableText =
    MutableText.of(LanguageText(key, args))

// 语言管理器对象，继承自Configurable
object LanguageManager : Configurable("lang") {

    // 当前语言标识符，如果用户没有覆盖则使用游戏默认语言
    val languageIdentifier: String
        get() = overrideLanguage.ifBlank { mc.options.language }

    // 用户可以覆盖的游戏语言
    var overrideLanguage by text("OverrideLanguage", "")

    // 通用语言标识符
    private const val COMMON_UNDERSTOOD_LANGUAGE = "en_us"

    // 已知的语言列表
    val knownLanguages = arrayOf(
        "en_us",
        "de_de",
        "ja_jp",
        "zh_cn",
        "ru_ru",
        "ua_ua",
        "en_pt",
        "pt_br"
    )
    private val languageMap = mutableMapOf<String, ClientLanguage>()

    /**
     * 加载预定义的所有语言，并存储在assets中。
     * 如果某个语言文件未找到，将记录错误日志。
     * 
     * 语言文件存储在assets/minecraft/liquidbounce/lang目录下，加载后存储在[languageMap]中
     */
    fun loadLanguages() {
        for (language in knownLanguages) {
            runCatching {
                val languageFile = javaClass.getResourceAsStream("/assets/liquidbounce/lang/$language.json")
                val translations = decode<HashMap<String, String>>(languageFile.reader().readText())

                languageMap[language] = ClientLanguage(translations)
            }.onSuccess {
                logger.info("Loaded language $language")
            }.onFailure {
                logger.error("Failed to load language $language", it)
            }
        }
    }

    // 获取当前语言对象，如果未找到则使用通用语言
    fun getLanguage() = languageMap[languageIdentifier] ?: languageMap[COMMON_UNDERSTOOD_LANGUAGE]

    // 获取通用语言对象
    fun getCommonLanguage() = languageMap[COMMON_UNDERSTOOD_LANGUAGE]

    // 检查通用语言中是否存在指定键的翻译
    fun hasFallbackTranslation(key: String) =
        languageMap[COMMON_UNDERSTOOD_LANGUAGE]?.hasTranslation(key) ?: false

}

// 客户端语言类，继承自Language
class ClientLanguage(private val translations: Map<String, String>) : Language() {

    // 根据键获取翻译文本
    private fun getTranslation(key: String) = translations[key]

    /**
     * 获取指定键的翻译文本。
     * 如果未找到翻译，将使用备用翻译。
     * 如果备用翻译也未找到，则返回键本身。
     * 
     * 使用此方法时需注意避免栈溢出。
     * 建议使用[getTranslation]方法。
     */
    override fun get(key: String, fallback: String?) = getTranslation(key)
        ?: LanguageManager.getCommonLanguage()?.getTranslation(key)
        ?: fallback
        ?: key

    // 检查是否存在指定键的翻译
    override fun hasTranslation(key: String) = translations.containsKey(key)

    // 检查是否为从右到左的语言
    override fun isRightToLeft() = false

    // 重新排序文本
    override fun reorder(text: StringVisitable) = OrderedText { visitor ->
        text.visit({ style, string ->
            if (TextVisitFactory.visitFormatted(string, style, visitor)) {
                Optional.empty()
            } else {
                StringVisitable.TERMINATE_VISIT
            }
        }, Style.EMPTY).isPresent
    }

}
