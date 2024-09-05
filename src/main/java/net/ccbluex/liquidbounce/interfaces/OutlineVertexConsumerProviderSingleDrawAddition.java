package net.ccbluex.liquidbounce.interfaces;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;

import javax.annotation.Nullable;

// 定义一个接口，用于获取仅绘制到轮廓帧缓冲区的顶点消费者
public interface OutlineVertexConsumerProviderSingleDrawAddition {
    /**
     * {@link net.minecraft.client.render.OutlineVertexConsumerProvider#getBuffer(RenderLayer)} 创建一个消费者，
     * 该消费者既绘制到轮廓帧缓冲区，也绘制到原始帧缓冲区。
     * <p>
     * 如果你只想绘制到轮廓帧缓冲区，请使用此方法。
     */
    @Nullable
    VertexConsumer liquid_bounce_getSingleDrawBuffers(RenderLayer layer);
}