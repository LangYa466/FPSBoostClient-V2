package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa466
 * @since 2/4/2025
 */
public class GUIOpenAnimation extends Module {
    public GUIOpenAnimation() {
        super("GUIOpenAnimation", "打开GUI动画");
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
