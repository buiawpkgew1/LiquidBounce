/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2024 CCBlueX
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
 *
 *
 */

package net.ccbluex.liquidbounce.utils.client.vfp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.screen.base.ProtocolSelectionScreen;
import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.utils.client.ClientProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 兼容ViaFabricPlus的兼容层
 * <p>
 * 在调用这些方法之前，请确保ViaFabricPlus已加载
 */
public enum VfpCompatibility {

    INSTANCE;

    /**
     * 禁用冲突的ViaFabricPlus选项
     */
    public void unsafeDisableConflictingVfpOptions() {
        try {
            VisualSettings visualSettings = VisualSettings.global();

            // 1 == off, 0 == on
            visualSettings.enableSwordBlocking.setValue(1);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to disable conflicting options", throwable);
        }
    }

    /**
     * 获取协议版本
     * @return 客户端协议版本
     */
    public ClientProtocolVersion unsafeGetProtocolVersion() {
        try {
            ProtocolVersion version = ProtocolTranslator.getTargetVersion();
            return new ClientProtocolVersion(version.getName(), version.getVersion());
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to get protocol version", throwable);
            return null;
        }
    }

    /**
     * 获取所有协议版本
     * @return 客户端协议版本数组
     */
    public ClientProtocolVersion[] unsafeGetProtocolVersions() {
        try {
            var protocols = ProtocolVersion.getProtocols()
                    .stream()
                    .filter(version -> version.getVersionType() == VersionType.RELEASE)
                    .map(version -> new ClientProtocolVersion(version.getName(), version.getVersion()))
                    .toArray(ClientProtocolVersion[]::new);

            ArrayUtils.reverse(protocols);
            return protocols;
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to get protocol versions", throwable);
            return new ClientProtocolVersion[0];
        }
    }

    /**
     * 打开ViaFabricPlus协议选择界面
     */
    public void unsafeOpenVfpProtocolSelection() {
        try {
            var currentScreen = MinecraftClient.getInstance().currentScreen;
            if (currentScreen == null) {
                currentScreen = new TitleScreen();
            }

            ProtocolSelectionScreen.INSTANCE.open(currentScreen);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to open ViaFabricPlus screen", throwable);
        }
    }

    /**
     * 选择协议版本
     * @param protocolId 协议ID
     */
    public void unsafeSelectProtocolVersion(int protocolId) {
        try {
            if (!ProtocolVersion.isRegistered(protocolId)) {
                throw new IllegalArgumentException("Protocol version is not registered");
            }

            ProtocolVersion version = ProtocolVersion.getProtocol(protocolId);
            ProtocolTranslator.setTargetVersion(version);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to select protocol version", throwable);
        }
    }

    /**
     * 检查是否为1.8版本
     * @return 是否为1.8版本
     */
    public boolean isEqual1_8() {
        try {
            var version = ProtocolTranslator.getTargetVersion();

            // Check if the version is equal to 1.8
            return version.equalTo(ProtocolVersion.v1_8);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to check if old combat", throwable);
            return false;
        }
    }

    /**
     * 检查是否为1.8或更早版本
     * @return 是否为1.8或更早版本
     */
    public boolean isOlderThanOrEqual1_8() {
        try {
            var version = ProtocolTranslator.getTargetVersion();

            // Check if the version is older or equal than 1.8
            return version.olderThanOrEqualTo(ProtocolVersion.v1_8);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to check if old combat", throwable);
            return false;
        }
    }

    /**
     * 检查是否为1.7.10或更早版本
     * @return 是否为1.7.10或更早版本
     */
    public boolean isOlderThanOrEqual1_7_10() {
        try {
            var version = ProtocolTranslator.getTargetVersion();

            // Check if the version is older or equal than 1.7.10
            return version.olderThanOrEqualTo(ProtocolVersion.v1_7_6);
        } catch (Throwable throwable) {
            LiquidBounce.INSTANCE.getLogger().error("Failed to check if old combat", throwable);
            return false;
        }
    }

}