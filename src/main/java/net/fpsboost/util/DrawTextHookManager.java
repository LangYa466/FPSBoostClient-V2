package net.fpsboost.util;

import net.fpsboost.module.impl.NameProtect;

/**
 * @author LangYa466
 * @since 1/26/2025
 */
public class DrawTextHookManager {
    public static DrawTextHook hookMethod(String text) {
        try {
            final DrawTextHook drawTextHook = new DrawTextHook(text);
            RankManager.hook(drawTextHook);
            NameProtect.hook(drawTextHook);
            OBSChecker.hook(drawTextHook);
            return drawTextHook;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DrawTextHook("Error");
    }
}
