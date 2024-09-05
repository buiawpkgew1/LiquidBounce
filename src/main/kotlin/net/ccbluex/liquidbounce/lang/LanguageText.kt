package net.ccbluex.liquidbounce.lang

import net.minecraft.text.TranslatableTextContent

// 定义一个名为 LanguageText 的类，继承自 TranslatableTextContent
// 该类用于处理多语言文本的翻译内容
class LanguageText(key: String, args: Array<out Any>) :
    TranslatableTextContent(key, null, args)
