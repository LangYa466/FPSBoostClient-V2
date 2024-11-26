package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa
 * @since 2024/9/5 17:14
 */
public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "自动疾跑", "Auto Sprint","自动疾跑");
    }

    @Override
    public void onUpdate() {
        mc.gameSettings.keyBindSprint.setKeyDown(true);
    }
}
