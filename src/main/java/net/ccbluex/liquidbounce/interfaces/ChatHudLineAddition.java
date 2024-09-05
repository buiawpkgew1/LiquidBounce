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
package net.ccbluex.liquidbounce.interfaces;

/**
 * 对 {@link net.minecraft.client.gui.hud.ChatHudLine} 的扩展接口。
 */
public interface ChatHudLineAddition {

    /**
     * 设置消息的计数。
     * 该计数表示此消息在 {@link net.ccbluex.liquidbounce.features.module.modules.misc.ModuleBetterChat} 中已经发送的次数。
     */
    void liquid_bounce$setCount(int count);

    /**
     * 获取存储在此行中的计数。
     */
    @SuppressWarnings("unused")
    int liquid_bounce$getCount();

}