package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.font.FontManager;

/**
 * @author LangYa
 * @since 2024/12/25 23:23
 */
public class BetterFont extends Module {
    public BetterFont() {
        super("BetterFont", "更好的MC字体");
        setEnable(true);
    }

    public static boolean enable;
    private static int tempHeight;

    @Override
    public void onEnable() {
        enable = true;
        tempHeight = mc.fontRendererObj.FONT_HEIGHT;
        mc.fontRendererObj.FONT_HEIGHT = FontManager.hanYi().getHeight();
        super.onEnable();
    }

    public static void isGUI(boolean isMC) {
        if (isMC) {
            enable = false;
            mc.fontRendererObj.FONT_HEIGHT = FontManager.hanYi().getHeight();
        } else {
            enable = true;
            mc.fontRendererObj.FONT_HEIGHT = tempHeight;
        }
    }


    @Override
    public void onDisable() {
        enable = false;
        super.onDisable();
    }
}
