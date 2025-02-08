package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa
 * @since 2024/11/27 20:59
 */
public class NoDestroyEffects extends Module {
    public NoDestroyEffects() {
        super("NoDestroyEffects", "没有挖掘粒子效果");
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
