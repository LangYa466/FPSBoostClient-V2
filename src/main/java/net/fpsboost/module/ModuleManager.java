package net.fpsboost.module;

import net.fpsboost.Wrapper;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.handler.MessageHandler;
import net.fpsboost.module.impl.*;
import net.fpsboost.screen.musicPlayer.MusicPlayerModule;
import net.fpsboost.socket.ClientIRC;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager implements Wrapper {
    public static final List<Module> modules = new CopyOnWriteArrayList<>();

    public static void init() {
        modules.addAll(Arrays.asList(
                new ClickGUIModule(), new Sprint(), new OldAnimation(), new NameProtect(),
                new BetterChat(), new NoMissHitDelay(), new HideScoreboardRect(),
                BossBar.INSTANCE, new BlockOverlay(), new ItemPhysic(), MotionBlur.INSTANCE,
                new NoHurtCam(), ClientIRC.INSTANCE, new FullBright(),
                ClientSettings.INSTANCE, new MoreParticles(), new SmokeCrosshair(),
                new NoDestroyEffects(), new Projectile(), new MinimizedBobbing(),
                HitColor.INSTANCE, new AttackEffects(), new SmoothGUIZoom(),
                new RenderMyNameTag(), BetterEnchantment.INSTANCE, new BetterNameTag(),
                new TargetCircle(), new ClickSounds(), new RectMode(), new CustomModel(),
                new CustomWorldTime(), new CustomHitBox(), new BetterInventory(),
                new ChatCopy(), new GUIOpenAnimation(), Perspective.INSTANCE,
                new HidePlantBlock(), new DragonWings(), new SkinLayers3D(),
                new HideRunningParticles(), new MusicPlayerModule()
        ));

        // 特殊模块设置
        if (!ClientIRC.INSTANCE.enable) ClientIRC.INSTANCE.toggle();
        ClientSettings.INSTANCE.enable = true;

        // 排序
        sortModules();
    }

    public static void sortModules() {
        modules.sort(Comparator.comparing(Module::getDisplayName));
    }

    public static boolean isEnabled(Class<?> moduleClass) {
        return modules.stream().anyMatch(module -> module.getClass() == moduleClass && module.enable);
    }

    // 渲染方法 - 2D渲染
    public static void moduleRender2D() {
        MessageHandler.onRender2D();
        modules.stream().filter(module -> module.enable).forEach(Module::onRender2D);
    }

    // 渲染方法 - 3D渲染
    public static void moduleRender3D() {
        modules.stream().filter(module -> module.enable).forEach(Module::onRender3D);
    }

    public static void moduleUpdate() {
        modules.stream().filter(module -> module.enable).forEach(Module::onUpdate);
        AttackHandler.onUpdate();
    }

    public static void moduleWorldLoad() {
        modules.stream().filter(module -> module.enable).forEach(Module::onWorldLoad);
    }

    public static void moduleKeyBind(int inputKeyCode) {
        for (Module module : modules) {
            if (inputKeyCode == module.keyCode) {
                module.toggle();
                break; // 只需触发一个模块
            }
        }
    }
}
