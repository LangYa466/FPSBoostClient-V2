package net.fpsboost.module;

import net.fpsboost.module.impl.TestModule;

import java.util.ArrayList;

/**
 * @author LangYa
 * @since 2024/8/30 21:19
 */
public class ModuleManager {
    public static final ArrayList<Module> modules = new ArrayList<>();

    public static void init() {
        modules.add(new TestModule());
    }

    public static void moduleRender2D() {
        for (Module module : modules) {
            module.onRender2D();
        }
    }

    public static void moduleKeyBind(int inputKeyCode) {
        for (Module module : modules) {
            if (inputKeyCode == module.keyCode) {
                module.toggle();
            }
        }
    }
}
