package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

/**
 * @author LangYa466
 * @since 1/24/2025
 */
public class CustomWorldTime extends Module {
    public CustomWorldTime() {
        super("CustomWorldTime", "自定义世界时间");
    }

    private final NumberValue time = new NumberValue("时间", "Time", 14000, 0, 24000, 100);

    public static boolean isEnable;

    @Override
    public void onEnable() {
        isEnable = true;
    }

    @Override
    public void onDisable() {
        isEnable = false;
    }

    public static boolean onPacketRev(Packet<?> packet) {
        return isEnable && (packet instanceof S03PacketTimeUpdate);
    }

    @Override
    public void onUpdate() {
        if (mc.theWorld != null) mc.theWorld.setWorldTime(time.getValue().longValue());
        super.onUpdate();
    }
}
