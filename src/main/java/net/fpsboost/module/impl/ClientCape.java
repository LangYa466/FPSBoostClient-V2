package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.CapeUtil;
import net.fpsboost.util.ClientInputGUI;

/**
 * @author LangYa
 * @since 2024/11/24 22:59
 */
public class ClientCape extends Module {
    public ClientCape() {
        super("ClientCape", "赞助披风");
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
