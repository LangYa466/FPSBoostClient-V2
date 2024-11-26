package net.fpsboost.util;

import net.fpsboost.Wrapper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

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


    public static void drawRectWithOutline(int x,int y,int width,int height,int color,int outlineColor) {
        Gui.drawRect(x,y,x + width,y + height,color);
        drawOutline(x,y,width,height,outlineColor);
    }

    public static int drawCenterString(String text, int x, int y, int color) {
        return mc.fontRendererObj.drawString(text,(x - getStringWidth(text) / 2),y,color);
    }

    public static int drawString(String text, int x, int y, int color) {
        return mc.fontRendererObj.drawString(text,x,y,color);
    }

    public static int drawString(String text, int x, int y, int color,boolean textShadow) {
        return mc.fontRendererObj.drawString(text,x,y,color,textShadow);
    }

    public static int drawString(String text, int x, int y, Color color) {
       return drawString(text,x,y,color.getRGB());
    }

    public static int drawStringWithRounded(String text, int x, int y) {
        return drawStringWithRounded(text,x,y,true);
    }

    public static int drawStringWithRounded(String text, int x, int y, boolean bg) {
        int width = getStringWidth(text);
        int height = mc.fontRendererObj.FONT_HEIGHT;
        if (bg) RenderUtil.drawRect(x, y, width + 4,height,bgColor);
        RenderUtil.drawString(text,x + 1, y, pressbgColor);
        return width + 4;
    }

    public static int drawText(String text, int x, int y, boolean bg, int bgColor,int textColor,boolean textShadow) {
        int width = getStringWidth(text);
        int height = mc.fontRendererObj.FONT_HEIGHT;
        if (bg) RenderUtil.drawRect(x - 2, y - 2, width + 8,height + 4,bgColor);
        if (!GameSettings.forceUnicodeFont) RenderUtil.drawString(text,x + 1, y + 1, textColor,textShadow); else RenderUtil.drawString(text,x + 1, y, textColor,textShadow);
        return width + 8;
    }

    public static int drawStringWithOutline(String text, int x, int y, int bgColor,int color) {
        int width = getStringWidth(text);
        int height = mc.fontRendererObj.FONT_HEIGHT;
        RenderUtil.drawOutline(x, y, width + 4,height,bgColor);
        RenderUtil.drawString(text,x + 2, y, color);
        return width + 4;
    }


    public static int drawStringWithShadow(String text, int x, int y, int color) {
        return mc.fontRendererObj.drawStringWithShadow(text,x,y,color);
    }

    public static int drawStringWithShadow(String text, int x, int y, Color color) {
        return drawStringWithShadow(text,x,y,color.getRGB());
    }

    public static int drawStringWithShadowAndRounded(String text, int x, int y) {
        int width = getStringWidth(text);
        int height = mc.fontRendererObj.FONT_HEIGHT;
        RenderUtil.drawRect(x, y, width,height,pressbgColor);
        int textX = getStringWidth(text);
        RenderUtil.drawStringWithShadow(text,x + textX, y + height / 2 + 1, bgColor);
        return width + 4;
    }


    public static int getStringWidth(String text) {
        return mc.fontRendererObj.getStringWidth(text);
    }

    public static void resetColor() {
        GlStateManager.resetColor();
    }

    public static Color reAlpha(Color color,float alpha) {
        return new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha);
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float imgWidth, float imgHeight) {
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
        GlStateManager.disableBlend();
    }
}
