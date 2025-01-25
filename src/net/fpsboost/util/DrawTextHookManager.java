package net.fpsboost.util;

/**
 * @author LangYa466
 * @since 1/26/2025
 */
public class DrawTextHookManager {
    public static DrawTextHook hookMethod(String text) {
        final DrawTextHook drawTextHook = new DrawTextHook(text);
        RankManager.hook(drawTextHook);
        return drawTextHook;
    }
}
