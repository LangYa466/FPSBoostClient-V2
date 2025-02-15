package net.fpsboost.module;

import net.fpsboost.Wrapper;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.handler.MessageHandler;
import net.fpsboost.module.impl.*;
import net.fpsboost.socket.ClientIRC;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager implements Wrapper {
    private static final LinkedHashMap<Class<?>, Module> moduleMap = new LinkedHashMap<>();
    public static List<Module> modules = new CopyOnWriteArrayList<>();

    public static void init() {
        // 批量添加模块
        addModules(
                new ClickGUIModule(), new Sprint(), new OldAnimation(), new NameProtect(),
                new BetterChat(), new NoMissHitDelay(), new HideScoreboardRect(),
                BossBar.INSTANCE, new BlockOverlay(), new ItemPhysic(), MotionBlur.INSTANCE,
                new NoHurtCam(), ClientIRC.INSTANCE, new ClientCape(), new FullBright(),
                ClientSettings.INSTANCE, new MoreParticles(), new SmokeCrosshair(),
                new NoDestroyEffects(), new Projectile(), new MinimizedBobbing(),
                HitColor.INSTANCE, new AttackEffects(), new SmoothGUIZoom(),
                new RenderMyNameTag(), BetterEnchantment.INSTANCE, new BetterNameTag(),
                new TargetCircle(), new ClickSounds(), new RectMode(), new CustomModel(),
                new CustomWorldTime(), new CustomHitBox(), new BetterInventory(),
                new ChatCopy(), new GUIOpenAnimation(), Perspective.INSTANCE,
                new HidePlantBlock(), new DragonWings(), new SkinLayers3D(),
                new HideRunningParticles()
        );

        // 特殊模块设置
        if (!ClientIRC.INSTANCE.enable) ClientIRC.INSTANCE.toggle();
        ClientSettings.INSTANCE.enable = true;

        // 初始化模块列表（仅排序一次）
        modules = new ArrayList<>(moduleMap.values());
        sortModules();
    }

    public static void sortModules() {
        modules.sort(Comparator.comparing(Module::getDisplayName));
    }

    private static void addModules(Module... newModules) {
        for (Module module : newModules) {
            moduleMap.put(module.getClass(), module);
        }
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
                break; // 一旦找到目标模块，立即终止循环 之前有个臭傻逼说我不break 我草他吗的 没遇见过BUG吗、、
            }
        }
    }
}
