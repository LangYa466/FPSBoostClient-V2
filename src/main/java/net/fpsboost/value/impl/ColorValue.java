package net.fpsboost.value.impl;

import lombok.Getter;
import lombok.Setter;
import net.fpsboost.module.Module;
import net.fpsboost.screen.clickgui.utils.HSBColor;
import net.fpsboost.value.Value;

import java.awt.*;

@Getter
@Setter
public class ColorValue extends Value<HSBColor> {

    private BooleanValue rainbow;
    private NumberValue rainbowSpeed;
    private Module module;

    public ColorValue(String cnName, String name, Color value, Module m) {
        super(cnName, name, new HSBColor(value.getRed(), value.getGreen(), value.getBlue(), value.getAlpha()));
        module = m;
        init();
    }

    public void setValue2(String input) {
        String[] split = input.split(":");
        if (split.length < 4)
            return;
        this.setValue(new HSBColor(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]),
                Integer.parseInt(split[3])));
    }

    public void init() {
        this.rainbow = new BooleanValue((this.getName()) + ("彩虹色"), (this.getName()) + ("Rainbow"), false);
        this.rainbowSpeed = new NumberValue(
                (this.getName())
                        + ("彩虹速度"),
                this.getName()
                        + ("RainbowSpeed"),
                3, 1, 10, 1);
        module.values.add(this.rainbow);
        module.values.add(this.rainbowSpeed);
    }

    @Override
    public HSBColor getValue() {
        if (this.rainbow.getValue()) {
            float speed = this.rainbowSpeed.getValue().floatValue();
            float hue = System.currentTimeMillis() % (int) ((1 - speed / 15.0) * 2000);
            hue /= (int) ((1 - speed / 15.0) * 2000);
            super.getValue().setHue(hue);
        }
        return super.getValue();
    }

    public Integer getValueC() {
        return getValue().getColor().getRGB();
    }

}
/*
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

    public ColorValue(String cnName,String name, int color) {
        super(cnName,name, color);
        this.setColor(color);
    }

    public ColorValue(String cnName,String name, Color color) {
        super(cnName,name, color.getRGB());
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

 */