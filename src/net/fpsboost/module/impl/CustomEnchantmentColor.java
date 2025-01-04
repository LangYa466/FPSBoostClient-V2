package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.ColorValue;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/8/26 22:12
 */
public class CustomEnchantmentColor extends Module {

    public CustomEnchantmentColor() {
        super("CustomEnchantmentColor","自定义附魔颜色");
    }

    public static final CustomEnchantmentColor INSTANCE = new CustomEnchantmentColor();
    public final ColorValue color = new ColorValue("自定义颜色","Custom Color",new Color(-8372020),this);

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