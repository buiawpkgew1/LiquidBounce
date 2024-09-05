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
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.lang.reflect.Type

// 用于序列化和反序列化 Minecraft 物品的适配器
object ItemValueSerializer : JsonSerializer<Item>, JsonDeserializer<Item> {

    // 将 Minecraft 物品序列化为 JSON 元素
    override fun serialize(src: Item, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(Registries.ITEM.getId(src).toString())
    }

    // 从 JSON 元素反序列化为 Minecraft 物品
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Item {
        return Registries.ITEM.get(Identifier.tryParse(json.asString))
    }

}
