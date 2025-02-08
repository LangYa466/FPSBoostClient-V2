package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa466
 * @since 2025/1/3
 */
public class RenderMyNameTag extends Module {
    public RenderMyNameTag() {
        super("RenderMyNameTag", "渲染自己的名称");
    }
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
