package net.fpsboost.socket;

import net.fpsboost.util.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * @author LangYa466
 * @since 2025/1/20
 */
public class SocketClient {
    private final Socket socket;
    private final IRCHandler handler;
    public static Object transport;
    private final ConcurrentHashMap<String, String> userToIGNMap = new ConcurrentHashMap<>(); // 并发安全的映射
    private final PrintWriter writer;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool(); // 线程池管理

    public SocketClient(String host, int port, IRCHandler handler) throws IOException {
        this.handler = handler;
        this.socket = new Socket(host, port);
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        start();
    }

    // 启动客户端并监听消息
    public void start() {
        threadPool.execute(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                handler.onConnected();

                // 发送用户名与 IGN
                sendMessage(handler.getUsername() + ":" + handler.getInGameUsername());

                String message;
                while ((message = reader.readLine()) != null) {
                    // 处理映射关系消息
                    if (message.equals("GET_USERS_REQUEST")) {
                        sendAllUsers();
                    } else {
                        String[] parts = message.split(":");
                        if (parts.length == 2) {
                            userToIGNMap.put(parts[0], parts[1]);  // 存储映射关系
                        }
                        handler.onMessage(message);
                    }
                }
            } catch (IOException e) {
                handler.onDisconnected("错误: " + e.getMessage());
            }
        });
    }

    // 检查用户是否存在
    public boolean isUser(String ign) {
        return userToIGNMap.containsValue(ign);
    }

    // 发送所有用户与 IGN 映射
    private void sendAllUsers() {
        userToIGNMap.forEach((username, ign) -> sendMessage(username + ":" + ign));
    }

    // 获取指定用户名的 IGN
    public String getName(String userName) {
        return userToIGNMap.get(userName);
    }

    // 发送消息
    public void sendMessage(String message) {
        synchronized (writer) { // 确保线程安全
            writer.println(message);
        }
    }

    // 关闭连接
    public void close() {
        try {
            socket.close();
            handler.onDisconnected("连接已关闭");
        } catch (IOException e) {
            Logger.error("关闭连接失败: " + e.getMessage());
        }
    }
}
