package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.CapeUtil;

import javax.swing.*;

/**
 * @author LangYa
 * @since 2024/11/24 22:59
 */
public class ClientCape extends Module {
    public ClientCape() {
        super("ClientCape", "切换客户端披风","You can switch your client cape here","没有卡密的别开");
    }

    @Override
    public void onEnable() {
        String cape = JOptionPane.showInputDialog("输入你的披风网址(包括https://):");
        if (cape == null)return;
        CapeUtil.setCape(cape);
        enable = false;
        super.onEnable();
    }
}
