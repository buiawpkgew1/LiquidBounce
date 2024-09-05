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

import com.viaversion.viaversion.api.minecraft.BlockPosition;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

import static de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator.getPlayNetworkUserConnection;

/**
 * 兼容 ViaFabricPlus 在协议 1.8 上的层
 * <p>
 * 在未检查 ViaFabricPlus 是否加载以及当前协议是否为 1.8 的情况下，请勿调用这些方法
 */
public enum VfpCompatibility1_8 {

    INSTANCE;

    /**
     * 发送告示牌更新包
     *
     * @param blockPos 告示牌位置
     * @param lines    告示牌内容，必须为4行
     * @throws IllegalArgumentException 如果行数不等于4，则抛出此异常
     */
    public void sendSignUpdate(final BlockPos blockPos, final String[] lines) throws IllegalArgumentException {
        if (lines.length != 4) {
            throw new IllegalArgumentException("Lines length does not match 4");
        }

        writePacket(ServerboundPackets1_8.SIGN_UPDATE, packet -> {
            packet.write(Types.BLOCK_POSITION1_8, new BlockPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ()));

            for (var line : lines) {
                packet.write(Types.STRING, line);
            }
        });
    }

    /**
     * 写入并发送数据包
     *
     * @param packetType 数据包类型
     * @param writer     数据包写入器
     * @throws IllegalStateException 如果当前协议不是 1.8，则抛出此异常
     */
    private void writePacket(ServerboundPacketType packetType, Consumer<PacketWrapper> writer) {
        if (!VfpCompatibility.INSTANCE.isEqual1_8()) {
            throw new IllegalStateException("Not on 1.8 protocol");
        }

        var packet = PacketWrapper.create(packetType, getPlayNetworkUserConnection());
        writer.accept(packet);
        packet.sendToServerRaw();
    }

}