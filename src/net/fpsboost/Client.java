package net.fpsboost;

import net.fpsboost.command.CommandManager;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.IRCUtil;
import net.fpsboost.util.network.WebUtil;
import net.fpsboost.value.ValueManager;

/**
 * @author LangYa
 * @since 2024/8/30 21:17
 */
public class Client implements Wrapper {
    public static final String name = "FPSBoost-V2";
    public static final String version = "1.3";
    public static boolean isOldVersion;

    public static void initClient() {
        ModuleManager.init();
        ElementManager.init();
        ValueManager.init();
        CommandManager.init();
        ConfigManager.init();
        IRCUtil.init();

        // TODO GuiWelcome
        // if (ConfigManager.isFirst) mc.displayGuiScreen(new GuiWelcome());

        // Version Check
        isOldVersion = !(WebUtil.get("http://122.51.47.169/versionwithv2.txt").contains(version));
    }
}

