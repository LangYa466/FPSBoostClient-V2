package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa466
 * @since 2/9/2025
 */
public class HideRunningParticles extends Module {
    public HideRunningParticles() {
        super("HideRunningParticles", "隐藏奔跑粒子");
    }

    //如果直接在mc里面的方法里面直接获取会浪费性能
    public static boolean isEnable;

    @Override
    public void onEnable() {
        isEnable = true;
    }

    @Override
    public void onDisable() {
        isEnable = false;
    }
}
