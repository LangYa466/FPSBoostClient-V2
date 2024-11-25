package net.fpsboost.module.impl;

import dev.jnic.annotations.JNICInclude;
import net.fpsboost.module.Module;
import net.fpsboost.util.CapeUtil;
import net.fpsboost.util.network.WebUtil;

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
        String kami = JOptionPane.showInputDialog("输入你的卡密 没有请去群公告获取");
        if (WebUtil.get("http://122.51.47.169/v2Cape.php?kami=" + kami).contains("OK")) {
            CapeUtil.setCape(JOptionPane.showInputDialog("输入你的披风网址(包括https://)"));
        } else {
            JOptionPane.showMessageDialog(null,"卡密不存在");
        }
        enable = false;
        super.onEnable();
    }
}
