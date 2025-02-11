package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.NumberValue;
import org.lwjgl.input.Keyboard;

/**
 * @author LangYa466
 * @since 2025/1/3
 */
public class SmoothGUIZoom extends Module {
    public SmoothGUIZoom() {
        super("Smooth GUI Zoom", "丝滑C键");
    }

    public static final NumberValue speedValue = new NumberValue("动画速度", "Speed", 0.4F, 1F, 0.1F, 0.1F);
    private final BooleanValue smoothMouseValue = new BooleanValue("丝滑鼠标", "SmoothMouse", true);

    public static boolean isEnable;
    public static boolean isKeyDown;

    public static float decreasedSpeed(float current, float start, float target, float speed) {
        float k = speed / (start - target);
        return current + (k * (current - start) + speed) * (start > target ? -1 : 1);
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen != null) return;
        isKeyDown = Keyboard.isKeyDown(mc.gameSettings.ofKeyBindZoom.getKeyCode());
        mc.gameSettings.smoothCamera = (smoothMouseValue.getValue() && isKeyDown);
        super.onUpdate();
    }

    @Override
    public void onEnable() {
        isEnable = true;
    }

    @Override
    public void onDisable() {
        isEnable = false;
    }
}
