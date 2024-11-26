package net.fpsboost.module.impl;

import dev.jnic.annotations.JNICInclude;
import net.fpsboost.module.Module;
import net.fpsboost.util.CapeUtil;

import javax.swing.*;

/**
 * @author LangYa
 * @since 2024/11/24 22:59
 */
@JNICInclude
public class ClientCape extends Module {
    public ClientCape() {
        super("ClientCape", "切换客户端披风","没有卡密的别开");
    }

    @Override
    public void onEnable() {
        CapeUtil.setCape(JOptionPane.showInputDialog("输入你的披风网址(包括https://)"));
        enable = false;
        super.onEnable();
    }
}
