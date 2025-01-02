package net.fpsboost.module;

import net.fpsboost.Wrapper;
import net.fpsboost.element.ElementManager;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.module.impl.*;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author LangYa
 * @since 2024/8/30 21:19
 */
public class ModuleManager implements Wrapper {
    public static final ArrayList<Module> modules = new ArrayList<>();

    public static void init() {
        modules.add(new ClickGUI());
        modules.add(new Sprint());
        modules.add(new OldAnimation());
        modules.add(new NameProtect());
        modules.add(new HideGuiChatRect());
        modules.add(new NoMissHitDelay());
        modules.add(new HideScoreboardRect());
        modules.add(new BossBar());
        // modules.add(new AutoLuGuan());
        modules.add(new BlockOverlay());
        modules.add(new ItemPhysic());
        modules.add(MotionBlur.INSTANCE);
        modules.add(new NoHurtCam());
        modules.add(IRC.INSTANCE);
        if (!IRC.INSTANCE.enable) IRC.INSTANCE.toggle();
        modules.add(new ClientCape());
        modules.add(new FullBright());
        modules.add(ClientSettings.INSTANCE);
        ClientSettings.INSTANCE.enable = true;
        modules.add(new MoreParticles());
        modules.add(new SmokeCrosshair());
        modules.add(new NoDestroyEffects());
        modules.add(new Projectile());
        modules.add(new MinimizedBobbing());
        modules.add(new HitColor());
        modules.add(new AttackEffects());
        modules.add(FreeLook.INSTANCE);
        modules.add(new ZoomChatAnimation());
        modules.add(new SmoothGUIZoom());
        modules.add(new RenderMyNameTag());
        // modules.add(new BetterFont());这个模块有点bug
        // modules.add(new TargetCircle()); 这个模块有点bug

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
        if (mc.currentScreen != null) return;
    //    if (Client.isOldVersion) RenderUtil.drawStringWithShadow("你正在使用旧版本",5,5,-1);
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

    public static void moduleWorldLoad() {
        for (Module module : modules) {
            if (!module.enable) continue;
            module.onWorldLoad();
        }
    }

    public static void moduleKeyBind(int inputKeyCode) {
        FreeLook.INSTANCE.onKey();
        for (Module module : modules) {
            if (inputKeyCode == module.keyCode) {
                module.toggle();
            }
        }
    }
}
