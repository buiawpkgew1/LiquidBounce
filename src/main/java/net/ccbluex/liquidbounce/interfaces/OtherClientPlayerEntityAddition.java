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

import net.minecraft.entity.damage.DamageSource;

/**
 * 对 {@link net.minecraft.client.network.OtherClientPlayerEntity} 的扩展接口。
 */
public interface OtherClientPlayerEntityAddition {

    /**
     * 允许实体接收伤害。
     * 在 {@link net.ccbluex.liquidbounce.features.command.commands.client.fakeplayer.FakePlayer} 中使用。
     */
    @SuppressWarnings("unused")
    boolean liquid_bounce$actuallyDamage(DamageSource source, float amount);

}