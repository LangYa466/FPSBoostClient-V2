package net.fpsboost.module;

import net.fpsboost.module.impl.*;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author LangYa
 * @since 2024/8/30 21:19
 */
public class ModuleManager {
    public static final ArrayList<Module> modules = new ArrayList<>();

    public static void init() {
        modules.add(new TestModule());
        modules.add(new Sprint());

        modules.sort(Comparator.comparing(module -> module.name));
    }

    public static void moduleRender2D() {
        for (Module module : modules) {
            module.onRender2D();
        }
    }

    public static void moduleUpdate() {
        for (Module module : modules) {
            module.onUpdate();
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
