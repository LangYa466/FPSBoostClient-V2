package net.fpsboost.socket;

import cn.langya.Logger;
import net.fpsboost.Wrapper;
import net.fpsboost.module.Module;
import net.fpsboost.util.ChatUtil;
import net.fpsboost.util.RankUtil;
import net.minecraft.util.EnumChatFormatting;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author LangYa466
 * @since 2025/1/3
 */
public class ClientIRC extends Module implements Wrapper {
    public static final ClientIRC INSTANCE = new ClientIRC();
    private static Socket socket;
    private static BufferedReader serverInput;
    private static PrintWriter out;
    private static final Set<String> userIgnList = ConcurrentHashMap.newKeySet();  // 使用线程安全的集合
    private static boolean initiated = false;
    private static boolean added = false;
    private boolean updated = false;
    private static int errorIndex;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();  // 使用线程池

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

        executorService.submit(() -> {
            try {
                initClient();

                String message;
                while ((message = serverInput.readLine()) != null) {
                    Logger.debug(message);
                    processMessage(message);
                }
            } catch (IOException e) {
                Logger.error("发生错误: {}", e.getMessage());
                handleConnectionError();
            } finally {
                closeResources();
            }
        });

        Logger.info("链接服务器后端成功!");
        initiated = true;
    }

    private static void initClient() throws IOException {
        socket = new Socket("103.79.187.250", 11451);
        serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    private static void handleConnectionError() {
        errorIndex++;
        if (errorIndex >= 10) {
            Logger.error("错误次数超过限制，进程即将结束...");
            System.exit(1);
        }
        try {
            initClient();
        } catch (IOException e) {
            Logger.error("重新连接失败: {}", e.getMessage());
        }
    }

    private static void closeResources() {
        try {
            if (serverInput != null) {
                serverInput.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            Logger.error("关闭资源时发生错误: {}", e.getMessage());
        }
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
        message = message.replace("-", "");
        String ign = mc.session.getUsername();
        RankUtil.getRanksAsync();
        String sendMessage = String.format(".message %s %s", ign, message);
        Logger.debug("发送消息: {}", sendMessage);
        send(sendMessage);
        return true;
    }

    private static void processMessage(String message) {
        if (message.startsWith(".message")) {
            processChatMessage(message);
        } else if (message.startsWith(".addUser")) {
            processUserJoin(message);
        } else if (message.startsWith(".addIGN")) {
            processAddIgn(message);
        } else {
            Logger.warn("收到了未知的信息: {}", message);
        }
    }

    private static void processChatMessage(String message) {
        String[] parts = message.split("\\s+", 3);
        if (parts.length == 3) {
            String ign = parts[1];
            String content = parts[2];
            String rank = RankUtil.getRank(ign);
            if (rank == null) rank = "普通用户";

            EnumChatFormatting rankColor = getRankColor(rank);

            ChatUtil.addMessageWithClient("[客户端内置聊天] " + rankColor + "[" + rank + "] " + EnumChatFormatting.GREEN + "(" + ign + ")" + EnumChatFormatting.RESET + ": " + content);
        } else {
            Logger.warn("收到了未知的信息: {}", message);
        }
    }

    private static EnumChatFormatting getRankColor(String rank) {
        switch (rank) {
            case "Admin":
                return EnumChatFormatting.GOLD;
            case "Media":
                return EnumChatFormatting.DARK_RED;
            case "MVP++":
                return EnumChatFormatting.RED;
            case "SVIP":
                return EnumChatFormatting.DARK_GREEN;
            case "VIP":
                return EnumChatFormatting.GREEN;
            default:
                return EnumChatFormatting.BLUE;
        }
    }

    private static void processUserJoin(String message) {
        String[] parts = message.split("\\s+", 2);
        if (parts.length == 2) {
            String userName = parts[1];
            ChatUtil.addMessageWithClient("[客户端内置聊天] " + EnumChatFormatting.GREEN + userName + EnumChatFormatting.RESET + " 上线了");
        } else {
            Logger.warn("收到了未知的信息: {}", message);
        }
    }

    private static void processAddIgn(String message) {
        String[] parts = message.split("\\s+", 3);
        if (parts.length == 2) {
            String ign = parts[1];
            userIgnList.add(ign);
        } else {
            Logger.warn("收到了非法的数据包: {}", message);
        }
    }

    public static boolean isUser(String ign) {
        return userIgnList.contains(ign);
    }
}
