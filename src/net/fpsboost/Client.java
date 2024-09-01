package net.fpsboost;

import net.fpsboost.command.CommandManager;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.value.ValueManager;

/**
 * @author LangYa
 * @since 2024/8/30 21:17
 */
public class Client implements Wrapper{
    public static final String name = "FPSBoost-V2";
    public static final String version = "1.0";

    public static void initClient() {
        ModuleManager.init();
        ValueManager.init();
        ElementManager.init();
        CommandManager.init();
    }

}

