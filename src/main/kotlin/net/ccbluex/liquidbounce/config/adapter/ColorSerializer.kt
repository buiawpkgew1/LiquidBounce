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

import com.google.gson.*
import net.ccbluex.liquidbounce.render.engine.Color4b
import java.awt.Color
import java.lang.reflect.Type

// 颜色序列化器，用于将 Color4b 对象序列化为 JSON 格式，以及从 JSON 格式反序列化为 Color4b 对象
object ColorSerializer : JsonSerializer<Color4b>, JsonDeserializer<Color4b> {

    // 将 Color4b 对象序列化为 JSON 元素
    override fun serialize(src: Color4b, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toRGBA())
    }

    // 从 JSON 元素反序列化为 Color4b 对象
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Color4b {
        return Color4b(Color(json.asInt, true))
    }

}
