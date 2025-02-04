package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.entity.EntityPlayerSP;

public class OldAnimation extends Module {
    public static BooleanValue oldRod = new BooleanValue("鱼竿动画","OldRod Animation", true);
    public static BooleanValue oldBlock = new BooleanValue("放置动画","Place Animation", true);
    public static BooleanValue blockHit = new BooleanValue("防砍动画","BlockHit Animation", true);
    public static BooleanValue oldBow = new BooleanValue("弓箭动画","Bow Animation", true);
    public static BooleanValue oldSwing = new BooleanValue("挥手动画","Swing Animation", true);
    public static BooleanValue oldUsing = new BooleanValue("吃东西动画","Using Animation", true);
    public static NumberValue usingY = new NumberValue("吃东西高度(吃东西动画)","Using Food Y",0.3,1.5,-2,.1);
    public static BooleanValue oldSneak = new BooleanValue("潜行动画","Sneak Animation", false);
    public static BooleanValue oldSmoothSneak = new BooleanValue("潜行动画丝滑(需先开潜行动画)","Sneak Smooth Animation", false);

    public OldAnimation() {
        super("1.7Animation", "1.7动画","Animation for 1.7 version", "旧版本动画");
    }

    //如果直接在mc里面的方法里面直接获取会浪费性能
    public static boolean isEnabled;

    @Override
    public void onEnable() {
        isEnabled = true;
    }

    @Override
    public void onDisable() {
        isEnabled = false;
    }

    private static final float START_HEIGHT = 1.62f;
    private static final float END_HEIGHT = 1.54f;

    private static float eyeHeight;
    private static float lastEyeHeight;

    public static float getEyeHeight(float partialTicks) {
        if (!isEnabled) return mc.thePlayer.getEyeHeight();
        if (!oldSmoothSneak.getValue()) {
            return eyeHeight;
        }

        return lastEyeHeight + (eyeHeight - lastEyeHeight) * partialTicks;
    }

    @Override
    public void onUpdate() {
        lastEyeHeight = eyeHeight;

        final EntityPlayerSP player = mc.thePlayer;
        if (player == null) {
            eyeHeight = START_HEIGHT;
            return;
        }

        if (player.isSneaking()) {
            eyeHeight = END_HEIGHT;
        } else if (!oldSneak.getValue()) {
            eyeHeight = START_HEIGHT;
        } else if (eyeHeight < START_HEIGHT) {
            float delta = START_HEIGHT - eyeHeight;
            delta *= 0.4F;
            eyeHeight = START_HEIGHT - delta;
        }
    }
}
