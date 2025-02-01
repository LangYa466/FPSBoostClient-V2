package net.fpsboost;

import cn.langya.Logger;
import net.fpsboost.command.CommandManager;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.screen.GuiI18n;
import net.fpsboost.screen.GuiRectMode;
import net.fpsboost.screen.GuiWelcome;
import net.fpsboost.socket.ClientIRC;
import net.fpsboost.util.CapeUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.util.network.WebUtil;
import net.fpsboost.util.texfix.TextureFix;
import net.fpsboost.value.ValueManager;
import net.minecraft.client.gui.GuiMainMenu;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author LangYa
 * @since 2024/8/30 21:17
 */
public class Client implements Wrapper {
    public static final String name = "FPSBoost-V2";
    public static final String version = "2.10";
    public static boolean isOldVersion;
    public static boolean isDev = false;
    public static final String web = "https://api.fpsboost.cn:444/";
    public static GuiMainMenu guimainMenu;

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static void initClient() throws IOException {
        // Set log file
        setLogFile();

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

    private static void setLogFile() {
        File logFile = new File(ConfigManager.dir, isDev ? "debug.log" : "error.log");
        Logger.setLogFile(logFile.getAbsolutePath());
    }

    private static void initComponents() {
        ModuleManager.init();
        ElementManager.init();
        ValueManager.init();
        CommandManager.init();
        ConfigManager.init();
        ClientIRC.init();
        CapeUtil.init();
        FontManager.init();
        TextureFix.init();
    }

    private static void checkForUpdates() throws IOException {
        String latestVersion = Objects.requireNonNull(WebUtil.getNoCache(web + "versionwithv2.txt")).trim();
        Logger.info((!ClientSettings.isChinese ? "The latest version of FPSBoost is: " + latestVersion : "后端最新版本: " + latestVersion));
        isOldVersion = !version.contains(latestVersion);
        if (isOldVersion && isWindows()) {
            File autoUpdateJarFile = new File("versions\\FPSBoost_V2\\AutoUpdate.jar");
            Logger.info("Auto-update jar file: " + autoUpdateJarFile.getAbsolutePath());
            Runtime.getRuntime().exec("java -jar " + autoUpdateJarFile.getAbsolutePath());
            System.exit(0);
        }
    }

    public static void stopClient() {
        ConfigManager.saveAllConfig();
        Logger.shutdown();
    }
}
