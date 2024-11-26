package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.ChatUtil;

import static net.fpsboost.util.IRCUtil.transport;

/**
 * @author LangYa
 * @since 2024/11/24 下午3:59
 */
public class IRC extends Module {
    public static final IRC INSTANCE = new IRC();
    String msg;

    public IRC() {
        super("IRC","在线聊天","Internet Relay Chat","在线即时聊天");
    }

    @Override
    public void onDisable() {
        this.enable = true;
    }

    public static boolean sendIRCMessage(String message) {
        if (message.equals("-online")) return CheckOnline(message);
        if (message.startsWith("-")) {
            if (transport != null) transport.sendChat(message);
            return true;
        }
        return false;
    }
    public static boolean CheckOnline(String message) {
        if (message.startsWith("-online")) {
            if (transport != null) ChatUtil.addMessageWithClient("[客户端在线用户列表] : "+transport.userToIgnMap);
            return true;
        }
        return false;
    }

    @Override
    public void onWorldLoad() {
        if (transport != null) transport.sendInGameUsername();
    }
}