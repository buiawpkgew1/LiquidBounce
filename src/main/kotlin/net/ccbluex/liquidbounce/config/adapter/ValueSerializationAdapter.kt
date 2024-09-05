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
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.ccbluex.liquidbounce.config.Value
import java.lang.reflect.Type

// 用于将 Value 对象序列化为 JsonElement 的适配器
object ValueSerializationAdapter : JsonSerializer<Value<*>> {

    // 重写序列化方法，将 Value 对象转换为 JsonElement
    override fun serialize(src: Value<*>, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement {
        val obj = JsonObject()

        obj.addProperty("name", src.name)
        obj.add("value", context.serialize(src.inner))

        return obj
    }

}
