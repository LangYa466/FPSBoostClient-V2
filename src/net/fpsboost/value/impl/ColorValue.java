package net.fpsboost.value.impl;

import lombok.Getter;
import lombok.Setter;
import net.fpsboost.value.Value;

import java.awt.*;

@Getter
@Setter
public class ColorValue extends Value<Integer> {
    private int color;
    private boolean hasAlpha = true;

    public ColorValue(String cnNmae,String name, int color) {
        super(cnNmae,name, color);
        this.setColor(color);
    }

    public ColorValue(String cnNmae,String name, Color color) {
        super(cnNmae,name, color.getRGB());
        this.setColor(color.getRGB());
    }

    public boolean getHasAlpha() {
        return hasAlpha;
    }

    public int getRed() {
        return (color >> 16) & 0xFF;
    }

    public int getGreen() {
        return (color >> 8) & 0xFF;
    }

    public int getBlue() {
        return color & 0xFF;
    }

    public int getAlpha() {
        return (color >> 24) & 0xFF;
    }

    public void setRed(int red) {
        color = (color & 0xFF00FFFF) | (red << 16);
        this.setValue(color);
    }

    public void setGreen(int green) {
        color = (color & 0xFFFF00FF) | (green << 8);
        this.setValue(color);
    }

    public void setBlue(int blue) {
        color = (color & 0xFFFFFF00) | blue;
        this.setValue(color);
    }

    public void setAlpha(int alpha) {
        color = (color & 0x00FFFFFF) | (alpha << 24);
        this.setValue(color);
    }

    public Color getColorC() {
        return new Color(color);
    }
}
