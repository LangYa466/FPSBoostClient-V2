package net.fpsboost.socket;

import net.fpsboost.util.Logger;
import net.fpsboost.Wrapper;
import net.fpsboost.module.Module;
import net.fpsboost.util.RankUtil;

import java.io.*;

/**
 * @author LangYa466
 * @since 2025/1/3
 */
public class ClientIRC extends Module implements Wrapper {
    public static final ClientIRC INSTANCE = new ClientIRC();
    public static SocketClient handler;
    private static boolean initiated = false;

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
        handler.sendMessage("GET_USERS_REQUEST");
        RankUtil.getRanksAsync();
        super.onWorldLoad();
    }

    public static void init() {
        if (initiated) return;

        try {
            handler = new SocketClient("103.79.187.250", 11452, new IRCHandler() {
                @Override
                public void onMessage(String message) {
                    // 处理收到的消息
                    Logger.debug("Server Message: " + message);
                }

                @Override
                public void onDisconnected(String message) {
                    // 处理断开连接的情况
                    Logger.warn("断开连接: " + message);
                    SocketClient.transport = null;
                }

                @Override
                public void onConnected() {
                    // 处理连接成功的情况
                    Logger.info("链接服务器后端成功!!");
                }

                @Override
                public String getInGameUsername() {
                    // 返回游戏中的用户名，这里返回一个默认值
                    return mc.session.getUsername();
                }

                @Override
                public String getUsername() {
                    return mc.session.getUsername();
                }
            });

            handler.sendMessage("GET_USERS_REQUEST");

        } catch (IOException e) {
            Logger.error(e);
        }

        initiated = true;
    }

    public static boolean isUser(String ign) {
        return handler.isUser(ign);
    }
}
