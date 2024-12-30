package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa
 * @since 2024/9/4 19:28
 */
public class NameProtect extends Module {
    private static final String target = mc.getSession().getUsername();

    public NameProtect() {
        super("NameProtect", "名称保护","Protect your name and change it to 我是狼牙的狗", "全局替换你的名字为我是狼牙的狗");
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

    public static String onText(String text) {
        if (!enable) return text;
        // sb none text 例如kk craft就会这样
        if (text.isEmpty()) return text;

        StringBuilder sb = new StringBuilder(text);
        int index = sb.indexOf(target);

        while (index != -1) {
            String replacement = "我是狼牙的狗";
            sb.replace(index, index + target.length(), replacement);
            index += replacement.length();
            index = sb.indexOf(target, index);
        }

        return sb.toString();
    }
}
