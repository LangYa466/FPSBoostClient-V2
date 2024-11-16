package net.fpsboost.module;

import net.fpsboost.Client;
import net.fpsboost.element.ElementManager;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.module.impl.*;
import net.fpsboost.util.RenderUtil;

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
        modules.add(new HideGuiChatRect());
        modules.add(new NoMissHitDelay());
        modules.add(new HideScoreboardRect());
        modules.add(new AutoLuGuan());
        modules.add(new BlockOverlay());
        modules.add(new ItemPhysic());
        modules.add(new MotionBlur());

        modules.sort(Comparator.comparing(module -> module.name));
    }

    public static ArrayList<Module> getAllModules() {
        ArrayList<Module> allModules = new ArrayList<>();
        allModules.addAll(modules);
        allModules.addAll(ElementManager.elements);
        return allModules;
    }

    public static boolean isEnabled(Class<?> moduleClass) {
        for (Module module : modules) {
            if (module.getClass() == moduleClass) return module.enable;
        }

        return false;
    }

    public static void moduleRender2D() {
        if (Client.isOldVersion) RenderUtil.drawStringWithShadow("你正在使用旧版本",5,5,-1);
        for (Module module : modules) {
            if (!module.enable) continue;
            module.onRender2D();
        }
    }

    public static void moduleRender3D() {
        for (Module module : modules) {
            if (!module.enable) continue;
            module.onRender3D();
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
