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
import net.ccbluex.liquidbounce.config.Choice
import net.ccbluex.liquidbounce.config.ChoiceConfigurable
import java.lang.reflect.Type

// 用于将ChoiceConfigurable对象序列化为JsonElement的序列化器
object ChoiceConfigurableSerializer : JsonSerializer<ChoiceConfigurable<Choice>> {

    // 重写serialize方法，将ChoiceConfigurable对象序列化为JsonElement
    override fun serialize(
        src: ChoiceConfigurable<Choice>, typeOfSrc: Type, context: JsonSerializationContext
    ): JsonElement {
        val obj = JsonObject()

        obj.addProperty("name", src.name)
        obj.addProperty("active", src.activeChoice.choiceName)
        obj.add("value", context.serialize(src.inner))

        val choices = JsonObject()

        for (choice in src.choices) {
            choices.add(choice.name, context.serialize(choice))
        }

        obj.add("choices", choices)
        obj.add("valueType", context.serialize(src.valueType))

        return obj
    }

}
