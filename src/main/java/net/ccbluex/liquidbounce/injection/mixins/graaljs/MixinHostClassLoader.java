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

package net.ccbluex.liquidbounce.injection.mixins.graaljs;

import net.ccbluex.liquidbounce.utils.mappings.Remapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 将类名重映射为其混淆后的对应名称。
 *
 * 初始代码由 lit 编写
 */
@Mixin(targets = "com/oracle/truffle/host/HostClassLoader")
public class MixinHostClassLoader {

    /**
     * 重映射类名
     *
     * @param value 原始类名
     * @return 重映射后的类名
     */
    @ModifyVariable(method = "findClass", at = @At("HEAD"), argsOnly = true, remap = false)
    private String remapClassName(String value) {
        return Remapper.INSTANCE.remapClassName(value);
    }

}