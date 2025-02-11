package net.fpsboost.util;

import net.fpsboost.handler.MessageHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author LangYa466
 * @since 2/9/2025
 */
public class OBSChecker {
    public static boolean run = false;

    public static void init() {
        Thread obsCheckThread = new Thread(() -> {
            while (true) {
                try {
                    if (checkOBS()) {
                        run = true;
                        // 1.5s x 3
                        for (int i = 0; i < 3; i++) {
                            MessageHandler.addMessage("检测到OBS进程 自动开启防录制登录密码", MessageHandler.MessageType.Info);
                        }
                        // 10s x1
                        MessageHandler.addMessage("检测到OBS进程 自动开启防录制登录密码", MessageHandler.MessageType.Info, 10000);
                    }
                    Thread.sleep(5000);
                } catch (Exception e) {
                    Logger.error(e);
                }
            }
        });
        obsCheckThread.setDaemon(true);
        obsCheckThread.start();
    }

    public static DrawTextHook hook(DrawTextHook drawTextHook) {
        if (!run) return drawTextHook;
        String text = drawTextHook.getDisplayText();

        if (!text.startsWith("/") || (!text.contains("login") && !text.contains("register"))) {
            return drawTextHook;
        }

        // 替换 login 后面的内容
        text = text.replaceAll("(?i)(login\\s*).*", "$1******");

        // 替换 register 后面的内容
        text = text.replaceAll("(?i)(register\\s*).*", "$1******");

        drawTextHook.setDisplayText(text);
        return drawTextHook;
    }

    private static boolean checkOBS() {
        try {
            String line;
            Process process;

            // Windows 平台使用 tasklist
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                process = Runtime.getRuntime().exec("tasklist");
            } else { // Linux/macOS 使用 ps
                process = Runtime.getRuntime().exec("ps aux");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("obs")) {
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return false;
    }
}