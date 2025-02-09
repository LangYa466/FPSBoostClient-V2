package net.fpsboost.util;

import net.fpsboost.Wrapper;

/**
 * @author LangYa466
 * @since 2/9/2025
 */
public class MoveUtil implements Wrapper {
    public static double getBPS() {
        if (mc.thePlayer == null) return 0.0;

        double x = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double z = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        double speed = Math.sqrt(x * x + z * z) * 20;

        return Math.round(speed * 10.0) / 10.0; // 保留一位小数
    }
}
