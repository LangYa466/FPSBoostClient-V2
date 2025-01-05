package net.fpsboost;

import cn.imflowow.clickgui.ClickGui;
import cn.langya.Logger;
import net.fpsboost.command.CommandManager;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.screen.GuiI18n;
import net.fpsboost.screen.GuiWelcome;
import net.fpsboost.socket.ClientIRC;
import net.fpsboost.util.CapeUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.util.network.WebUtil;
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
    public static final String version = "1.9";
    public static boolean isOldVersion;
    public static boolean isDev = false;
    public static final String web = "https://api.fpsboost.cn:444/";

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    public static void initClient() throws IOException {
        File logFile = new File(ConfigManager.dir, "error.log");
        if (isDev) logFile = new File(ConfigManager.dir, "debug.log");
        Logger.setLogFile(logFile.getAbsolutePath());
        ModuleManager.init();
        ElementManager.init();
        ValueManager.init();
        CommandManager.init();
        ConfigManager.init();
        ClientIRC.init();
        CapeUtil.init();
        FontManager.init();

        if (ConfigManager.isFirst) mc.displayGuiScreen(new GuiWelcome());

        // Version check
        if (!isDev) {
            String latestVersion = Objects.requireNonNull(WebUtil.getNoCache(web + "version.txt")).trim();
            Logger.info((!ClientSettings.INSTANCE.cnMode.getValue() ? "The latest version of FPSBoost is: " + latestVersion : "后端最新版本: " + latestVersion));
            isOldVersion = !version.contains(latestVersion);
            if (isOldVersion && isWindows()) {
                File autoUpdateJarFile = new File("versions\\FPSBoost_V2\\AutoUpdate.jar");
                String path = autoUpdateJarFile.getAbsolutePath();
                Logger.info("Auto-update jar file: " + path);
                Runtime.getRuntime().exec("java -jar " + path);
                System.exit(0);
            }
        }

        // download background
        String url = web + "fj";
        if (GuiMainMenu.file.exists()) {
            String localImageContent = FileUtils.readFileToString(GuiMainMenu.file);
            if (localImageContent.contains("http") && localImageContent.contains("://")) {
                WebUtil.bindTextureWithUrl(url, "ClientBG");
            } else {
                WebUtil.bindLocalTexture(FileUtils.readFileToString(GuiMainMenu.file), "ClientBG");
            }
        } else {
            WebUtil.bindTextureWithUrl(url, "ClientBG");
        }

        mc.displayGuiScreen(new GuiI18n());
    }

    public static void stopClient() {
        ConfigManager.saveAllConfig();
        Logger.shutdown();
    }
}

