package net.fpsboost.util;

import net.fpsboost.Wrapper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/8/30 21:52
 */
public class RenderUtil extends ThemeUtil implements Wrapper {
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

    public static int drawString(String text, int x, int y, int color) {
        return mc.fontRendererObj.drawString(text,x,y,color);
    }

    public static int drawString(String text, int x, int y, Color color) {
       return drawString(text,x,y,color.getRGB());
    }

    public static int drawStringWithRounded(String text, int x, int y) {
        return drawStringWithRounded(text,x,y,true);
    }

    public static int drawStringWithRounded(String text, int x, int y, boolean bg) {
        int width = mc.fontRendererObj.getStringWidth(text);
        int height = mc.fontRendererObj.FONT_HEIGHT;
        if (bg) RenderUtil.drawRect(x, y, width + 4,height,bgColor);
        RenderUtil.drawString(text,x + 1, y, pressbgColor);
        return width + 4;
    }

    public static int drawStringWithShadow(String text, int x, int y, int color) {
        return mc.fontRendererObj.drawStringWithShadow(text,x,y,color);
    }

    public static int drawStringWithShadow(String text, int x, int y, Color color) {
        return drawStringWithShadow(text,x,y,color.getRGB());
    }

    public static int drawStringWithShadowAndRounded(String text, int x, int y) {
        int width = mc.fontRendererObj.getStringWidth(text);
        int height = mc.fontRendererObj.FONT_HEIGHT;
        RenderUtil.drawRect(x, y, width,height,pressbgColor);
        int textX = mc.fontRendererObj.getStringWidth(text);
        RenderUtil.drawStringWithShadow(text,x + textX, y + height / 2 + 1, bgColor);
        return width + 4;
    }


    public static int getStringWidth(String text) {
        return mc.fontRendererObj.getStringWidth(text);
    }

    public static void resetColor() {
        GlStateManager.resetColor();
    }
}
