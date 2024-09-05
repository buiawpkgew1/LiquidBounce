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
package net.ccbluex.liquidbounce.config.util

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// 运行时保留的注解，用于标记需要排除的字段
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Exclude

// 实现 ExclusionStrategy 接口的类，用于排除带有 Exclude 注解的字段
class ExcludeStrategy : ExclusionStrategy {
    // 判断是否跳过某个类，这里总是返回 false，表示不跳过任何类
    override fun shouldSkipClass(clazz: Class<*>?) = false
    // 判断是否跳过某个字段，如果字段带有 Exclude 注解则返回 true
    override fun shouldSkipField(field: FieldAttributes) = field.getAnnotation(Exclude::class.java) != null
}

/**
 * 解码 JSON 内容
 */
inline fun <reified T> decode(stringJson: String): T = Gson().fromJson(stringJson, object : TypeToken<T>() {}.type)
