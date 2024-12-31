package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa466
 * @since 2025/1/1
 */
public class MinimizedBobbing extends Module {
    public MinimizedBobbing() {
        super("MinimizedBobbing", "视角最小晃动");
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
