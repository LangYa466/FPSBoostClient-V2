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
        super("ClientCape", "切换客户端披风");
    }

    @Override
    public void onEnable() {
        enable = false;
        ClientInputGUI clientInputGUI = new ClientInputGUI(mc.displayWidth / 4, mc.displayHeight / 4);
        mc.displayGuiScreen(clientInputGUI);
        String cape = clientInputGUI.text;
        if (cape == null)return;
        CapeUtil.setCape(cape);
        super.onEnable();
    }
}
