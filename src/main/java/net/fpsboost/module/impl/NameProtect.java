package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.DrawTextHook;

/**
 * @author LangYa
 * @since 2024/9/4 19:28
 */
public class NameProtect extends Module {
    private static final String target = mc.getSession().getUsername();

    public NameProtect() {
        super("NameProtect", "名称保护", "Protect your name and change it to 我是狼牙的狗", "全局替换你的名字为我是狼牙的狗");
    }

    public static boolean enable;

    @Override
    public void onEnable() {
        enable = true;
    }

    @Override
    public void onDisable() {
        enable = false;
    }

    public static DrawTextHook hook(DrawTextHook drawTextHook) {
        String text = drawTextHook.getDisplayText();

        if (!enable) return drawTextHook;

        String replacement = "我是狼牙的狗";
        int targetLength = target.length();
        StringBuilder sb = new StringBuilder();

        int start = 0;
        int index = text.indexOf(target);

        while (index != -1) {
            sb.append(text, start, index).append(replacement);
            start = index + targetLength;
            index = text.indexOf(target, start);
        }

        sb.append(text, start, text.length());
        drawTextHook.setDisplayText(sb.toString());
        return drawTextHook;
    }
}
