package net.fpsboost.module;

import cn.imflowow.MessageManager;
import net.fpsboost.Wrapper;
import net.fpsboost.element.ElementManager;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.module.impl.*;
import net.fpsboost.socket.ClientIRC;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LangYa
 * @since 2024/8/30 21:19
 */
public class ModuleManager implements Wrapper {
    public static List<Module> modules = new ArrayList<>();

    public static void init() {
        addModule(new ClickGUIModule());
        addModule(new Sprint());
        addModule(new OldAnimation());
        addModule(new NameProtect());
        addModule(new HideGuiChatRect());
        addModule(new NoMissHitDelay());
        addModule(new HideScoreboardRect());
        addModule(new BossBar());
        // addModule(new AutoLuGuan());
        addModule(new BlockOverlay());
        addModule(new ItemPhysic());
        addModule(MotionBlur.INSTANCE);
        addModule(new NoHurtCam());
        addModule(ClientIRC.INSTANCE);
        if (!ClientIRC.INSTANCE.enable) ClientIRC.INSTANCE.toggle();
        addModule(new ClientCape());
        addModule(new FullBright());
        addModule(ClientSettings.INSTANCE);
        ClientSettings.INSTANCE.enable = true;
        addModule(new MoreParticles());
        addModule(new SmokeCrosshair());
        addModule(new NoDestroyEffects());
        addModule(new Projectile());
        addModule(new MinimizedBobbing());
        addModule(HitColor.INSTANCE);
        addModule(new AttackEffects());
        addModule(new SmoothGUIZoom());
        addModule(new RenderMyNameTag());
        addModule(CustomEnchantmentColor.INSTANCE);
        addModule(new BetterNameTag());
        // addModule(new BetterFont());这个模块有点bug
        // addModule(new TargetCircle()); 这个模块有点bug

        modules = moduleMap.values().stream()
                .sorted(Comparator.comparing(module -> module.name)) // 排序
                .collect(Collectors.toList());
    }

    public static ArrayList<Module> getAllModules() {
        ArrayList<Module> allModules = new ArrayList<>();
        allModules.addAll(modules);
        allModules.addAll(ElementManager.elements);
        return allModules;
    }

    private static final Map<Class<?>, Module> moduleMap = new HashMap<>();

    private static void addModule(Module module) {
        moduleMap.put(module.getClass(), module);
    }

    public static boolean isEnabled(Class<?> moduleClass) {
        Module module = moduleMap.get(moduleClass);
        return module != null && module.enable;
    }

    public static void moduleRender2D() {
        if (mc.currentScreen != null) return;
        MessageManager.onRender2D();
        modules.stream().filter(Module::isEnabled).forEach(Module::onRender2D);
    }

    public static void moduleRender3D() {
        modules.stream().filter(Module::isEnabled).forEach(Module::onRender3D);
    }

    public static void moduleUpdate() {
        modules.stream().filter(Module::isEnabled).forEach(Module::onUpdate);
        AttackHandler.onUpdate();
    }

    public static void moduleWorldLoad() {
        modules.stream().filter(Module::isEnabled).forEach(Module::onWorldLoad);
    }

    public static void moduleKeyBind(int inputKeyCode) {
        for (Module module : modules) {
            if (inputKeyCode == module.keyCode) {
                module.toggle();
            }
        }
    }
}
