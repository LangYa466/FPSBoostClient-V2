package net.fpsboost.value.impl;

import lombok.Getter;
import lombok.Setter;
import net.fpsboost.module.Module;
import net.fpsboost.screen.clickgui.utils.HSBColor;
import net.fpsboost.value.Value;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ColorValue extends Value<HSBColor> {
    private Module module;

    public ColorValue(String cnName, String name, Color value, Module m) {
        super(cnName, name, new HSBColor(value.getRed(), value.getGreen(), value.getBlue(), value.getAlpha()));
        this.module = m;
        init();
    }

    // 将 values 转换为 Map
    public Map<String, Value<?>> getValuesMap() {
        Map<String, Value<?>> valuesMap = new HashMap<>();
        module.values.forEach(value -> valuesMap.put(value.name, value));
        return valuesMap;
    }

    // 优化设置值的方法
    public void setValue2(String input) {
        String[] split = input.split(":");
        if (split.length == 4) {
            this.setValue(new HSBColor(Float.parseFloat(split[0]), Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]), Integer.parseInt(split[3])));
        }
    }

    private String rainbowValueName;
    private String rainbowSpeedValueName;

    private void init() {
        rainbowValueName = name + "Rainbow";
        rainbowSpeedValueName = name + "RainbowSpeed";

        module.values.add(new BooleanValue(cnName + "彩虹色", rainbowValueName, false));
        module.values.add(new NumberValue(cnName + "彩虹速度", rainbowSpeedValueName, 3, 1, 10, 1));
    }

    @Override
    public HSBColor getValue() {
        Map<String, Value<?>> valuesMap = getValuesMap();

        // 获取并处理彩虹效果
        BooleanValue rainbowValue = (BooleanValue) valuesMap.get(rainbowValueName);
        if (rainbowValue != null && rainbowValue.getValue()) {
            NumberValue rainbowSpeedValue = (NumberValue) valuesMap.get(rainbowSpeedValueName);
            if (rainbowSpeedValue != null) {
                float speed = rainbowSpeedValue.getValue().floatValue();
                float hue = System.currentTimeMillis() % (int) ((1 - speed / 15.0) * 2000);
                hue /= (int) ((1 - speed / 15.0) * 2000);

                // 设置最大饱和度和亮度
                float saturation = 1.0f;  // 最大饱和度
                float brightness = 1.0f;  // 最大亮度

                // 设置颜色
                super.getValue().setHue(hue);
                super.getValue().setSaturation(saturation);
                super.getValue().setBrightness(brightness);
            }
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