package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa
 * @since 2024/11/16 15:35
 */
public class NoHurtCam extends Module {
    public NoHurtCam() {
        super("NoHurtCam", "无受伤抖动","Disable the camera shake when taking damage.","关闭受击动画");
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
