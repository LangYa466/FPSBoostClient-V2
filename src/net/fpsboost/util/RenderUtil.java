package net.fpsboost.util;

import net.fpsboost.Wrapper;
import net.minecraft.client.gui.Gui;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/8/30 21:52
 */
public class RenderUtil implements Wrapper {
    public static void drawRect(int x,int y,int width,int height,int color) {
        Gui.drawRect(x,y,x + width,y + height,color);
    }

    public static void drawRect(int x, int y, int width, int height, Color color) {
        drawRect(x,y,width,height,color.getRGB());
    }

    public static void drawOutline(int x, int y, int width, int height, int color) {
        Gui gui = new Gui();
        gui.drawHorizontalLine(x, x + width, y, color);
        gui.drawHorizontalLine(x, x + width, y + height, color);

        gui.drawVerticalLine(x, y, y + height, color);
        gui.drawVerticalLine(x + width, y, y + height, color);
    }

    public static void drawString(String text, int x, int y, int color) {
        mc.fontRendererObj.drawString(text,x,y,color);
    }

    public static void drawString(String text, int x, int y, Color color) {
        drawString(text,x,y,color.getRGB());
    }

    public static void drawStringWithShadow(String text, int x, int y, int color) {
        mc.fontRendererObj.drawStringWithShadow(text,x,y,color);
    }

    public static void drawStringWithShadow(String text, int x, int y, Color color) {
        drawStringWithShadow(text,x,y,color.getRGB());
    }
}
