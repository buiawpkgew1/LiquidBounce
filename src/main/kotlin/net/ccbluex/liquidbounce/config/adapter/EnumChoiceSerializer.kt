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

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.ccbluex.liquidbounce.config.NamedChoice
import java.lang.reflect.Type

// 用于将 NamedChoice 枚举类型序列化为 JSON 的适配器
object EnumChoiceSerializer : JsonSerializer<NamedChoice> {

    // 重写 serialize 方法，将 NamedChoice 对象序列化为 JSON 元素
    override fun serialize(src: NamedChoice, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.choiceName)
    }

}
