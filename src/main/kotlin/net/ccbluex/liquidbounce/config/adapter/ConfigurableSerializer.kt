/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2024 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */

package net.ccbluex.liquidbounce.config.adapter

import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.ccbluex.liquidbounce.config.Configurable
import net.ccbluex.liquidbounce.config.Value
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.Module
import java.lang.reflect.Type

// 配置项序列化器，用于将 Configurable 对象序列化为 JsonObject
object ConfigurableSerializer : JsonSerializer<Configurable> {

    // 重写序列化方法，将 Configurable 对象转换为 JsonObject
    override fun serialize(
        src: Configurable,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ) = JsonObject().apply {
        addProperty("name", src.name)
        add("value", context.serialize(src.inner))
    }
}

/**
 * 自动配置项序列化器，用于将需要公开发布的 Configurable 对象序列化为 JsonObject
 * 而不是使用 [ConfigurableSerializer]
 */
object AutoConfigurableSerializer : JsonSerializer<Configurable> {

    // 重写序列化方法，将 Configurable 对象转换为 JsonObject，并过滤不需要公开的值
    override fun serialize(
        src: Configurable,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ) = JsonObject().apply {
        addProperty("name", src.name)
        add("value", context.serialize(src.inner.filter { checkIfInclude(it) }))
    }

    /**
     * 检查值是否应该包含在公开配置中
     */
    private fun checkIfInclude(value: Value<*>): Boolean {
        /**
         * 不包含不应该与其他用户共享的值
         */
        if (value.doNotInclude()) {
            return false
        }

        // 可能检查值是否为模块
        if (value is Module) {
            /**
             * 不包含高度用户个性化的模块
             */
            if (value.category == Category.RENDER || value.category == Category.CLIENT) {
                return false
            }
        }

        // 否则包含值
        return true
    }

}
