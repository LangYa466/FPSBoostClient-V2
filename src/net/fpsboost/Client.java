package net.fpsboost;

import net.fpsboost.util.Logger;
import net.fpsboost.command.CommandManager;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.screen.GuiI18n;
import net.fpsboost.screen.GuiWelcome;
import net.fpsboost.socket.ClientIRC;
import net.fpsboost.util.IconUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.util.network.WebUtil;
import net.fpsboost.util.texfix.TextureFix;
import net.fpsboost.value.ValueManager;
import net.minecraft.client.gui.GuiMainMenu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author LangYa
 * @since 2024/8/30 21:17
 */
public class Client implements Wrapper {
    public static final String name = "FPSBoost-V2";
    public static final String version = "2.12";
    public static boolean isOldVersion;
    public static boolean isDev = false;
    public static final String web = "https://api.fpsboost.cn:444/";
    public static GuiMainMenu guimainMenu;

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static void initClient() throws IOException {
        // Initialize components
        initComponents();

        // First time screen
        if (ConfigManager.isFirst) {
            mc.displayGuiScreen(new GuiWelcome());
        }

        // Version check
        if (!isDev) {
            checkForUpdates();
        }

        // Display the language settings screen
        if (!isDev)  mc.displayGuiScreen(new GuiI18n());
        guimainMenu = new GuiMainMenu();
    }

    private static void initComponents() {
        ModuleManager.init();
        ElementManager.init();
        ValueManager.init();
        CommandManager.init();
        ConfigManager.init();
        ClientIRC.init();
        FontManager.init();
        TextureFix.init();
    }

    private static void checkForUpdates() throws IOException {
        String latestVersion = Objects.requireNonNull(WebUtil.getNoCache(web + "versionwithv2.txt")).trim();
        Logger.info((!ClientSettings.isChinese ? "The latest version of FPSBoost is: " + latestVersion : "后端最新版本: " + latestVersion));
        Logger.info("当前客户端版本: {}",version);
        isOldVersion = !version.contains(latestVersion);
        if (isOldVersion && isWindows()) {
            showNotification("已打开自动更新程序!!");
            File autoUpdateJarFile = new File("versions\\FPSBoost_V2\\AutoUpdate.jar");
            Logger.info("Auto-update jar file: " + autoUpdateJarFile.getAbsolutePath());
            Runtime.getRuntime().exec("java -jar " + autoUpdateJarFile.getAbsolutePath());
            System.exit(0);
        }
    }
    public static void showNotification(String message) {
        showNotification(message, TrayIcon.MessageType.INFO);
    }

    public static void showNotification(String message, TrayIcon.MessageType messageType) {
        if (!SystemTray.isSupported()) {
            Logger.error("系统不支持托盘通知");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();
        Image image = null;

        try {
            image = ImageIO.read(Objects.requireNonNull(
                    IconUtil.class.getResourceAsStream("/assets/minecraft/client/icons/icon_150x150.png")
            ));
        } catch (IOException e) {
            Logger.error("无法加载图标：" + e.getMessage());
        }

        if (image == null) {
            Logger.error("托盘图标加载失败");
            return;
        }

        TrayIcon trayIcon = new TrayIcon(image, "系统通知");
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage("FPSBoost", message, messageType);
        } catch (AWTException e) {
            Logger.error("托盘通知错误：" + e.getMessage());
        }
    }

    public static void stopClient() {
        ConfigManager.saveAllConfig();
    }

    public static void openErrorLogsFile() {
        if (isWindows()) {
            File logsDir = new File("logs");
            File logFile = new File("logs","latest.log");
            try {
                showNotification("打开错误日志成功 有错误可以把文件发群里!!");
                Desktop.getDesktop().open(logsDir);
                Desktop.getDesktop().open(logFile);
            } catch (IOException e) {
                Logger.error("打开错误日志失败: {}",e.getMessage());
                showNotification(String.format("打开错误目录失败(请手动打开%s)!!", logsDir.getAbsolutePath()));
            }
        }
    }
}
