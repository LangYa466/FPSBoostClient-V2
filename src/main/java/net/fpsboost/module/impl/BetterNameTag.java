package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;

/**
 * @author LangYa466
 * @since 2025/1/5
 */
public class BetterNameTag extends Module {
    public BetterNameTag() {
        super("BetterNameTag", "更好的名称栏");
    }

    public static final BooleanValue textShadow = new BooleanValue("字体阴影", "TextShadow", true);
    public static final BooleanValue hideRect = new BooleanValue("隐藏背景", "HideBg", true);

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
