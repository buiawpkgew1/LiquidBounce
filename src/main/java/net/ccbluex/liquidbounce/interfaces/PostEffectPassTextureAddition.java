package net.ccbluex.liquidbounce.interfaces;

// 定义一个接口，用于在后期效果通道中添加纹理
public interface PostEffectPassTextureAddition {
    // 设置纹理采样器的方法，接受纹理名称和纹理ID作为参数
    void liquid_bounce$setTextureSampler(String name, int textureId);
}