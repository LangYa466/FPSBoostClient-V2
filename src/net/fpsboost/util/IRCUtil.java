package net.fpsboost.util;

import net.fpsboost.Wrapper;
import net.minecraft.util.EnumChatFormatting;
import us.cubk.irc.client.IRCHandler;
import us.cubk.irc.client.IRCTransport;

import java.io.IOException;

public class IRCUtil implements Wrapper {

    public static IRCTransport transport = null;

    public static void init() {
        try {
            transport = new IRCTransport("122.51.47.169", 8888, new IRCHandler() {
                @Override
                public void onMessage(String sender, String message) {
                    ChatUtil.addMessageWithClient("[客户端内置聊天]" + EnumChatFormatting.GREEN + "(" + transport.getIgn(sender) + ")" + EnumChatFormatting.RESET + ": " + message);
                }

                @Override
                public void onDisconnected(String message) {
                    System.out.println("客户端服务器断开了链接: " + message);
                    transport = null;
                }

                @Override
                public void onConnected() {
                    System.out.println("已成功链接到客户端服务器");
                }

                @Override
                public String getInGameUsername() {
                    if (mc.thePlayer == null) return "未知用户";
                    return mc.thePlayer.getName();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        transport.connect(mc.session.getUsername(), "123");
    }
}

