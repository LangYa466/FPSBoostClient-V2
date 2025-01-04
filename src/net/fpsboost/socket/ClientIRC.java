package net.fpsboost.socket;

import cn.langya.Logger;
import net.fpsboost.Wrapper;
import net.fpsboost.module.Module;
import net.fpsboost.util.ChatUtil;
import net.fpsboost.util.RankUtil;
import net.minecraft.util.EnumChatFormatting;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author LangYa466
 * @since 2025/1/3
 */
public class ClientIRC extends Module implements Wrapper {
    public static final ClientIRC INSTANCE = new ClientIRC();
    private static Socket socket;
    private static BufferedReader serverInput;
    private static PrintWriter out;
    private static final List<String> userIgnList = new CopyOnWriteArrayList<>();
    private static boolean initiated = false;
    private static boolean added = false;
    private boolean updated = false;

    public ClientIRC() {
        super("ClientIRC", "客户端在线聊天");
    }

    @Override
    public void onEnable() {
        init();
    }

    @Override
    public void onDisable() {
        this.enable = true;
    }

    @Override
    public void onUpdate() {
        if (updated) return;
        addUser();
        sendIgn();
        updated = true;
        super.onUpdate();
    }

    @Override
    public void onWorldLoad() {
        if (updated) sendIgn();
        super.onWorldLoad();
    }


    private static void send(String message) {
        out.println(message);
    }

    public static void init() {
        if (initiated) return;

        new Thread(() -> {
            try {
                //                socket = new Socket("localhost", 11451);
                socket = new Socket("103.79.187.250", 11451);
                serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String message;
                    try {
                        while ((message = serverInput.readLine()) != null) {
                            Logger.debug(message);
                            processMessage(message);
                        }
                    } catch (IOException e) {
                        Logger.error("接收消息时发生错误: {}", e.getMessage());
                    }
                }
            } catch (IOException e) {
                Logger.error("发生错误: {}", e.getMessage());
            }
        }).start();
        Logger.info("链接服务器后端成功!");
        initiated = true;
    }

    private static void addUser() {
        if (added) return;
        send(String.format(".addUser %s", mc.session.getUsername()));
        added = true;
    }

    private static void sendIgn() {
        send(String.format(".addIGN %s", mc.session.getUsername()));
    }

    public static boolean sendMessage(String message) {
        if (!message.startsWith("-") || mc.thePlayer == null) return false;
        message = message.replace("-","");
        String ign = mc.session.getUsername();
        RankUtil.getRanksAsync();
        String sendMessage = String.format(".message %s %s", ign, message);
        Logger.debug("发送消息: {}", sendMessage);
        send(sendMessage);
        return true;
    }

    private static void processMessage(String message) {
        if (message.startsWith(".message")) {
            // 限制最大分割为 3 部分，确保 content 可以包含空格
            String[] parts = message.split("\\s+", 3);

            if (parts.length == 3) {
                String ign = parts[1];
                int index = 0;
                StringBuilder content = new StringBuilder();
                for (String part : parts) {
                    index++;
                    if (index > 2) content.append(part);
                }

                RankUtil.getRanksAsync();
                String rank = RankUtil.getRank(ign);
                if (rank == null) rank = "普通用户";

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

                ChatUtil.addMessageWithClient("[客户端内置聊天] " + rankColor + "[" + rank + "] " + EnumChatFormatting.GREEN + "(" + ign + ")" + EnumChatFormatting.RESET + ": " + content);
            } else {
                Logger.warn("收到了未知的信息: {}", message);
            }
        } else if (message.startsWith(".addUser")) {
            // 处理获取在线玩家列表的消息
            Logger.debug(message);
            String[] parts = message.split("\\s+", 2);
            if (parts.length == 2) {
                Logger.debug(Arrays.toString(parts));
                String userName = parts[1];
                ChatUtil.addMessageWithClient("[客户端内置聊天] " + EnumChatFormatting.GREEN + userName + EnumChatFormatting.RESET + " 上线了");
            } else {
                Logger.warn("收到了未知的信息: {}", message);
            }
        } else if (message.startsWith(".addIGN")) {
            String[] parts = message.split("\\s+", 3);
            if (parts.length == 2) {
                String ign = parts[1];
                if (!userIgnList.contains(ign)) userIgnList.add(ign);
            } else {
                Logger.warn("收到了非法的数据包: {}", message);
            }
        } else {
            Logger.warn("收到了未知的信息: {}", message);
        }
    }

    public static boolean isUser(String ign) {
        return userIgnList.contains(ign);
    }
}
