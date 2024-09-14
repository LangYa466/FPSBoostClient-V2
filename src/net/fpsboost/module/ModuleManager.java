package net.fpsboost.module;

import net.fpsboost.handler.AttackHandler;
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
        modules.add(new ClickGUI());
        modules.add(new Sprint());
        modules.add(new OldAnimation());
        modules.add(new NameProtect());

        modules.sort(Comparator.comparing(module -> module.name));
    }

    public static void moduleRender2D() {
        for (Module module : modules) {
            if (!module.enable) continue;
            module.onRender2D();
        }
    }

    public static void moduleUpdate() {
        for (Module module : modules) {
            if (!module.enable) continue;
            module.onUpdate();
        }
        AttackHandler.onUpdate();
    }

    public static void moduleKeyBind(int inputKeyCode) {
        for (Module module : modules) {
            if (inputKeyCode == module.keyCode) {
                module.toggle();
            }
        }
    }
}
