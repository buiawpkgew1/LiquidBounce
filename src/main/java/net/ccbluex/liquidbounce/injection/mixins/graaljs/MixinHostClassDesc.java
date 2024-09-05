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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 将类方法和字段名称映射到它们的混淆对应项。
 *
 * 初始代码由 lit 编写
 */
@Mixin(targets = "com/oracle/truffle/host/HostClassDesc")
public abstract class MixinHostClassDesc {

    @Shadow
    public abstract Class<?> getType();

    /**
     * 修改字段名称以映射到混淆后的名称。
     *
     * @param name 原始字段名称
     * @return 映射后的字段名称
     */
    @ModifyVariable(method = "lookupField(Ljava/lang/String;)Lcom/oracle/truffle/host/HostFieldDesc;",
            at = @At("HEAD"), argsOnly = true, index = 1, remap = false)
    private String remapFieldName(String name) {
        return Remapper.INSTANCE.remapField(getType(), name, true);
    }

    /**
     * 修改静态字段名称以映射到混淆后的名称。
     *
     * @param name 原始静态字段名称
     * @return 映射后的静态字段名称
     */
    @ModifyVariable(method = "lookupStaticField", at = @At("HEAD"), argsOnly = true, index = 1, remap = false)
    private String remapStaticFieldName(String name) {
        return Remapper.INSTANCE.remapField(getType(), name, true);
    }

    /**
     * 修改方法名称以映射到混淆后的名称。
     *
     * @param name 原始方法名称
     * @return 映射后的方法名称
     */
    @ModifyVariable(method = "lookupMethod(Ljava/lang/String;)Lcom/oracle/truffle/host/HostMethodDesc;",
            at = @At("HEAD"), argsOnly = true, index = 1, remap = false)
    private String remapMethodName(String name) {
        return Remapper.INSTANCE.remapMethod(getType(), name, true);
    }

    /**
     * 修改静态方法名称以映射到混淆后的名称。
     *
     * @param name 原始静态方法名称
     * @return 映射后的静态方法名称
     */
    @ModifyVariable(method = "lookupStaticMethod", at = @At("HEAD"), argsOnly = true, index = 1, remap = false)
    private String remapStaticMethodName(String name) {
        return Remapper.INSTANCE.remapMethod(getType(), name, true);
    }

}