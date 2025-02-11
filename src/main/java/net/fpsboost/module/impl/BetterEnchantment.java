package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/8/26 22:12
 */
public class BetterEnchantment extends Module {

    // 终于知道我之前写这个有啥用了 以后的我别给删了 这个ColorValue恶俗啊！！！！！
    public static final BetterEnchantment INSTANCE = new BetterEnchantment();

    public BetterEnchantment() {
        super("BetterEnchantment", "更好的附魔");
    }

    public static final BooleanValue disable = new BooleanValue("关闭附魔效果", "Disable Enchantment Effect", false);
    public final ColorValue color = new ColorValue("自定义颜色", "Custom Color", new Color(-8372020), this);

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