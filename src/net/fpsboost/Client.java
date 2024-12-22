package net.fpsboost;

import net.fpsboost.command.CommandManager;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.screen.GuiWelcome;
import net.fpsboost.util.CapeUtil;
import net.fpsboost.util.IRCUtil;
import net.fpsboost.util.network.WebUtil;
import net.fpsboost.value.ValueManager;
import net.minecraft.client.gui.GuiMainMenu;
import org.apache.commons.io.FileUtils;
import us.cubk.clickgui.ClickGuiScreen;

import java.io.IOException;
import java.util.Objects;

/**
 * @author LangYa
 * @since 2024/8/30 21:17
 */
public class Client implements Wrapper {
    public static final String name = "FPSBoost-V2";
    public static final String version = "1.7";
    public static boolean isOldVersion;
    public static boolean isDev = false;

    public static void initClient() throws IOException {
        ModuleManager.init();
        ElementManager.init();
        ValueManager.init();
        CommandManager.init();
        ConfigManager.init();
        IRCUtil.init();
        CapeUtil.init();
        ClickGuiScreen.INSTANCE.init();

        if (ConfigManager.isFirst) mc.displayGuiScreen(new GuiWelcome());

        // Version check
        if (!isDev) {
            String latestVersion = Objects.requireNonNull(WebUtil.getNoCache("http://122.51.47.169/versionwithv2.txt")).trim();
            System.out.printf((!ClientSettings.INSTANCE.cnMode.getValue() ? "The latest version of FPSBoost is: " + latestVersion : "后端最新版本: " + latestVersion) + "%n");
            isOldVersion = !version.contains(latestVersion);
        }

        // download background
        String url = "https://api.langya.ink/fj";
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
    }
}

