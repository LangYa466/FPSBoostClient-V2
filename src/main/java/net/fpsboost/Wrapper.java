package net.fpsboost;

import net.minecraft.client.Minecraft;

public interface Wrapper {
    Minecraft mc = Minecraft.getMinecraft();

    static boolean isNull() {
        return mc.thePlayer == null || mc.theWorld == null;
    }
}
