package net.ccbluex.liquidbounce.injection.mixins.graaljs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

// 混入类，用于修改 GraalVM 的 TruffleLanguage 类
@Mixin(targets = "com/oracle/truffle/api/TruffleLanguage")
public class MixinTruffleLanguage {

    /**
     * @author Senk Ju
     * @reason Prevent GraalVM from blocking multi threaded access to resources
     */
    // 重写方法，允许线程访问资源
    @Overwrite(remap = false)
    protected boolean isThreadAccessAllowed(Thread thread, boolean singleThreaded) {
        return true;
    }
}