package net.fpsboost.socket;

import net.fpsboost.util.Logger;
import net.fpsboost.Wrapper;
import net.fpsboost.module.Module;
import net.fpsboost.util.RankUtil;

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
    public void onWorldLoad() {
        sendIgn();
        RankUtil.getRanksAsync();
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

    private static void sendIgn() {
        send(String.format(".addIGN %s", mc.session.getUsername()));
    }

    private static void processMessage(String message) {
        if (message.startsWith(".addIGN")) {
            processAddIgn(message);
        } else {
            Logger.warn("收到了未知的信息: {}", message);
        }
    }

    private static void processAddIgn(String message) {
        String[] parts = message.split("\\s+", 3);
        if (parts.length == 2) {
            String ign = parts[1];
            if (userIgnList.contains(ign)) return;
            userIgnList.add(ign);
            Logger.debug("添加User IGN到list: {}", message);
        } else {
            Logger.warn("收到了非法的数据包: {}", message);
        }
    }

    public static boolean isUser(String ign) {
        return userIgnList.contains(ign);
    }
}
