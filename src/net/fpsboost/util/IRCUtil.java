package net.fpsboost.util;

import net.fpsboost.Wrapper;
import net.fpsboost.module.impl.ClientSettings;
import net.minecraft.util.EnumChatFormatting;
import us.cubk.irc.client.IRCHandler;
import us.cubk.irc.client.IRCTransport;

import java.io.IOException;
import java.util.Map;

public class IRCUtil implements Wrapper {

    public static IRCTransport transport = null;

    public static void init() {
        try {
            transport = new IRCTransport("127.0.0.1", 11451, new IRCHandler() {
                @Override
                public void onMessage(String rank, String message) {
                    EnumChatFormatting rankColor;
                    switch (rank) {
                        case "Admin":
                            rankColor = EnumChatFormatting.GOLD;
                            break;
                        case "Media":
                            rankColor = EnumChatFormatting.DARK_RED;
                            break;
                        case "MVP++":
                            rankColor = EnumChatFormatting.RED;
                            break;
                        case "SVIP":
                            rankColor = EnumChatFormatting.DARK_GREEN;
                            break;
                        case "VIP":
                            rankColor = EnumChatFormatting.GREEN;
                            break;
                        default:
                            rankColor = EnumChatFormatting.BLUE;
                            break;
                    }
                    ChatUtil.addMessageWithClient("[客户端内置聊天] " + rankColor + "[" + rank + "] " + EnumChatFormatting.GREEN + "(" + transport.getIgn(rank) + ")" + EnumChatFormatting.RESET + ": " + message);
                }

                @Override
                public void onDisconnected(String message) {
                    System.out.println("客户端服务器断开了链接: " + message);
                    transport = null;
                }

                @Override
                public void onConnected() {
                    if (ClientSettings.INSTANCE.cnMode.getValue()){
                        System.out.println("已成功链接到客户端服务器");
                    }else{
                        System.out.println("Connected to client server");
                    }
                }

                @Override
                public String getInGameUsername() {
                    if (mc.thePlayer == null) return "未知用户";
                    return mc.thePlayer.getName();
                }

                @Override
                public void getCapes(Map<String, String> capeMap) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        transport.connect("普通用户", "123");
    }
}

