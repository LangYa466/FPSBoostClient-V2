package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.NumberValue;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/07/26/下午3:24
 */
public class HitColor extends Module {
    public HitColor() {
        super("HitColor", "修改受伤颜色");
    }

    public final ColorValue colorValue = new ColorValue("背景颜色", "Background Color", new Color(0, 0, 0), this);
    public static final NumberValue alphaValue = new NumberValue("不透明度", "Opacity", 0.3, 0, 1, 0.01);
    public static final HitColor INSTANCE = new HitColor();
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