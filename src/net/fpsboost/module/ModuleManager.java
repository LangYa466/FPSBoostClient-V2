package net.fpsboost.module;

import net.fpsboost.handler.MessageHandler;
import net.fpsboost.Wrapper;
import net.fpsboost.element.ElementManager;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.module.impl.*;
import net.fpsboost.socket.ClientIRC;

import java.util.*;

public class ModuleManager implements Wrapper {
    private static final LinkedHashMap<Class<?>, Module> moduleMap = new LinkedHashMap<>();
    public static List<Module> modules = new ArrayList<>();

    public static void init() {
        // 批量添加模块
        addModules(
                new ClickGUIModule(), new Sprint(), new OldAnimation(), new NameProtect(),
                new HideGuiChatRect(), new NoMissHitDelay(), new HideScoreboardRect(),
                new BossBar(), new BlockOverlay(), new ItemPhysic(), MotionBlur.INSTANCE,
                new NoHurtCam(), ClientIRC.INSTANCE, new ClientCape(), new FullBright(),
                ClientSettings.INSTANCE, new MoreParticles(), new SmokeCrosshair(),
                new NoDestroyEffects(), new Projectile(), new MinimizedBobbing(),
                HitColor.INSTANCE, new AttackEffects(), new SmoothGUIZoom(),
                new RenderMyNameTag(), CustomEnchantmentColor.INSTANCE, new BetterNameTag(),
                new TargetCircle(), new ClickSounds(), new RectMode(), new CustomModel(),
                new CustomWorldTime()
        );

        // 特殊模块设置
        if (!ClientIRC.INSTANCE.enable) ClientIRC.INSTANCE.toggle();
        ClientSettings.INSTANCE.enable = true;

        // 初始化模块列表（仅排序一次）
        modules = new ArrayList<>(moduleMap.values());
        modules.sort(Comparator.comparing(module -> module.name));
    }

    private static void addModules(Module... newModules) {
        for (Module module : newModules) {
            moduleMap.put(module.getClass(), module);
        }
    }

    public static List<Module> getAllModules() {
        List<Module> allModules = new ArrayList<>(modules);
        allModules.addAll(ElementManager.elements); // 快速追加元素模块
        return allModules;
    }

    public static boolean isEnabled(Class<?> moduleClass) {
        Module module = moduleMap.get(moduleClass);
        return module != null && module.enable;
    }

    public static void moduleRender2D() {
        MessageHandler.onRender2D();
        for (Module module : modules) {
            if (module.enable) module.onRender2D();
        }
    }

    public static void moduleRender3D() {
        for (Module module : modules) {
            if (module.enable) module.onRender3D();
        }
    }

    public static void moduleUpdate() {
        for (Module module : modules) {
            if (module.enable) module.onUpdate();
        }
        AttackHandler.onUpdate();
    }

    public static void moduleWorldLoad() {
        for (Module module : modules) {
            if (module.enable) module.onWorldLoad();
        }
    }

    public static void moduleKeyBind(int inputKeyCode) {
        for (Module module : modules) {
            if (inputKeyCode == module.keyCode) {
                module.toggle();
                break; // 一旦找到目标模块，立即终止循环
            }
        }
    }
}
