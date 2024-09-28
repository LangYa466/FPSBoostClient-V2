package net.fpsboost;

import net.fpsboost.command.CommandManager;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.network.WebUtil;
import net.fpsboost.value.ValueManager;

/**
 * @author LangYa
 * @since 2024/8/30 21:17
 */
public class Client implements Wrapper{
    public static final String name = "FPSBoost-V2";
    public static final double version = 0.4;
    public static boolean isOldVersion = false;

    public static void initClient() {
        ModuleManager.init();
        ElementManager.init();
        ValueManager.init();
        CommandManager.init();
        ConfigManager.init();


        // Version Check
        String network = WebUtil.get("http://122.51.47.169/versionwithv2.txt");
        assert network != null;
        double version = Double.parseDouble(network);
        isOldVersion = (version > Client.version);
        System.out.printf("new version %s now version %s",version,Client.version);
    }

}

