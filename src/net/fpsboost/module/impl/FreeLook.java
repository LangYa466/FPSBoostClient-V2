package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.ChatUtil;
import org.lwjgl.input.Keyboard;

/**
 * @author LangYa466
 * @since 2025/1/3
 */
public class FreeLook extends Module {
    public static boolean cameraToggled = false;
    public static float rotationYaw;
    public static float rotationPitch;

    public static final FreeLook INSTANCE = new FreeLook();
    private boolean initRotations;

    public FreeLook() {
        super("FreeLook", "自由视角");
        setEnable(false);
    }

    @Override
    public void onWorldLoad() {
        setEnable(false);
        super.onWorldLoad();
    }

    public void onKey() {
        if (!enable) return;
        if(Keyboard.isKeyDown(keyCode) && !cameraToggled){
            cameraToggled = true;
            mc.gameSettings.thirdPersonView = 1;
        }
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;
        ChatUtil.addMessageWithClient("[FreeLook] 已开启");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;
        ChatUtil.addMessageWithClient("[FreeLook] 已开启");
        super.onDisable();
    }

    private void setRotations() {
        rotationYaw = mc.thePlayer.rotationYaw;
        rotationPitch = mc.thePlayer.rotationPitch;
        initRotations = true;
    }

    public static float[] getServerRotations() {
        return new float[] {rotationYaw, rotationPitch};
    }

    @Override
    public void onUpdate() {
        if(!Keyboard.isKeyDown(keyCode) && cameraToggled) {
            if (!initRotations) setRotations();
            cameraToggled = false;
            mc.gameSettings.thirdPersonView = 0;
        }
    }

    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
}