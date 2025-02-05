package net.fpsboost.socket;

import net.fpsboost.util.Logger;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LangYa466
 * @since 2025/1/20
 */
public class SocketClient {
    private final Socket socket;
    private final IRCHandler handler;
    public static Object transport;
    private final Map<String, String> userToIGNMap = new HashMap<>(); // 用于存储用户名与 IGN 的映射关系

    public SocketClient(String host, int port, IRCHandler handler) throws IOException {
        this.handler = handler;
        this.socket = new Socket(host, port);
        start();
    }

    // 启动客户端并监听消息
    public void start() {
        new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                // 连接成功时回调
                handler.onConnected();

                // 向服务器发送 "username:IGN"
                String username = handler.getUsername();
                String ign = handler.getInGameUsername();
                sendMessage(username + ":" + ign); // 发送用户名和IGN

                // 监听服务器发来的消息
                String message;
                while ((message = reader.readLine()) != null) {
                    // TODO 可以在这里赛你的加密
                    // 处理映射关系消息
                    if (message.equals("GET_USERS_REQUEST")) {
                        sendAllUsers(); // 发送所有用户
                    } else {
                        String[] parts = message.split(":");
                        if (parts.length == 2) {
                            String usernameReceived = parts[0];
                            String ignReceived = parts[1];
                            userToIGNMap.put(usernameReceived, ignReceived);  // 保存映射关系
                        }
                        handler.onMessage(message);
                    }
                }
            } catch (IOException e) {
                handler.onDisconnected("错误: " + e.getMessage());
            }
        }).start();  // 启动新线程
    }

    // 获取所有用户与IGN映射
    public void GetUsers() {
        sendMessage("GET_USERS_REQUEST"); // 向服务器请求用户映射
    }

    // 检查用户是否存在
    public boolean isUser(String ign) {
        GetUsers();
        return userToIGNMap.containsValue(ign);
    }

    // 发送所有用户与 IGN 映射
    private void sendAllUsers() {
        for (Map.Entry<String, String> entry : userToIGNMap.entrySet()) {
            sendMessage(entry.getKey() + ":" + entry.getValue());
        }
    }

    // 获取指定用户名的 IGN
    public String getName(String userName) {
        return userToIGNMap.get(userName);
    }

    // 发送消息
    public void sendMessage(String message) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(message);
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    // 关闭连接
    public void close() throws IOException {
        socket.close();
        handler.onDisconnected("连接已关闭");
    }
}