package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa466
 * @since 2025/1/3
 */
public class ZoomChatAnimation extends Module {
    public ZoomChatAnimation() {
        super("ZoomChatAnimation", "丝滑聊天动画");
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
