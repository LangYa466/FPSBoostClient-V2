package net.fpsboost.screen.clickgui.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class RenderEntity {
    public Minecraft mc = Minecraft.getMinecraft();

    public Minecraft getMinecraft() {
        return mc;
    }

    public void setMinecraft(Minecraft mc) {
        this.mc = mc;
    }

    public double getScaledWidth() {
        return new ScaledResolution(mc).getScaledWidth_double();
    }

    public ScaledResolution getScaledResolution() {
        return new ScaledResolution(mc);
    }

    public double getScaledHeight() {
        return new ScaledResolution(mc).getScaledHeight_double();
    }

    public double getScaledWidth(double scaled) {
        return new ScaledResolution(mc).getScaledWidth_double() / scaled;
    }

    public double getScaledHeight(double scaled) {
        return new ScaledResolution(mc).getScaledHeight_double() / scaled;
    }

    public int getGuiScaleFactor() {
        return new ScaledResolution(mc).getScaleFactor();
    }
}
