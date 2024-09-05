package net.ccbluex.liquidbounce.interfaces;

import net.minecraft.text.TextColor;

// 定义一个接口，用于扩展客户端文本颜色的功能
public interface ClientTextColorAdditions {
    // 检查是否绕过名称保护
    boolean liquid_bounce$doesBypassingNameProtect();

    // 获取绕过名称保护的文本颜色
    TextColor liquid_bounce$withNameProtectionBypass();

    @Deprecated
    /**
     * Please don't use this method, it is only for internal use.
     */
    // 设置是否绕过名称保护（已弃用，仅供内部使用）
    void liquid_bounce$setBypassingNameProtection(boolean bypassesNameProtect);
}