package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.NumberValue;

public class MotionBlur extends Module {

    public static final NumberValue blurAmount = new NumberValue("模糊度","Motion Blur Amount", 7, 10, 0, 0.1);

    public MotionBlur() {
        super("MotionBlur","动态模糊","Dynamic Blur(Warning: Turn off FastRender if you turn on this module)","快速渲染不能开 开了会失效");
    }
}
