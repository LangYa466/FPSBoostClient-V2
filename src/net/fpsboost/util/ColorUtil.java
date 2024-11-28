package net.fpsboost.util;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/11/28 19:16
 */
public class ColorUtil {
    public static Color rainbow(int speed, int index) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
    }
}
