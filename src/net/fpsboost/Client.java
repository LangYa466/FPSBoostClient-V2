package net.fpsboost;

import net.fpsboost.command.CommandManager;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.CapeUtil;
import net.fpsboost.util.IRCUtil;
import net.fpsboost.util.network.WebUtil;
import net.fpsboost.value.ValueManager;
import net.minecraft.client.gui.GuiMainMenu;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

/**
 * @author LangYa
 * @since 2024/8/30 21:17
 */
public class Client implements Wrapper {
    public static final String name = "FPSBoost-V2";
    public static final String version = "1.51";
    public static boolean isOldVersion;

    public static void initClient() throws IOException {
        ModuleManager.init();
        ElementManager.init();
        ValueManager.init();
        CommandManager.init();
        ConfigManager.init();
        IRCUtil.init();
        CapeUtil.init();

        // TODO GuiWelcome
        // if (ConfigManager.isFirst) mc.displayGuiScreen(new GuiWelcome());

        // Version check
        isOldVersion = !(WebUtil.get("http://122.51.47.169/versionwithv2.txt").contains(version));

        // download background
        String url = "https://t.mwm.moe/fj";
        if (GuiMainMenu.file.exists()) url = FileUtils.readFileToString(GuiMainMenu.file);
        WebUtil.bindTextureWithUrl(url,"ClientBG");
    }
}

