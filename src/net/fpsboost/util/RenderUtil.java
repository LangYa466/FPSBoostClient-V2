package net.fpsboost.util;

import net.fpsboost.Wrapper;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
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
        int height = mc.fontRendererObj.getHeight();
        if (bg) RenderUtil.drawRect(x, y, width + 4,height,bgColor);
        RenderUtil.drawString(text,x + 1, y, pressbgColor);
        return width + 4;
    }

    public static int drawText(String text, int x, int y, boolean bg, int bgColor,int textColor,boolean textShadow,boolean clientFont) {
        int width = clientFont ? FontManager.hanYi().getStringWidth(text) : mc.fontRendererObj.getStringWidth(text);
        int height = clientFont ? FontManager.hanYi().getHeight() : mc.fontRendererObj.getHeight();
        int width1 = clientFont ? width + 6 : width + 8;
        if (bg) RenderUtil.drawRect(x - 2, y - 2, width1,height + 4,bgColor);
        if (!clientFont) {
            if (!GameSettings.forceUnicodeFont) RenderUtil.drawString(text, x + 1, y + 1, textColor, textShadow);
            else RenderUtil.drawString(text, x + 1, y, textColor, textShadow);
        } else {
            FontManager.hanYi().drawString(text, x + 1, y - 1, textColor, textShadow);
        }
        return width1;
    }

    public static int drawText(String text, int x, int y, boolean bg, int bgColor,int textColor,boolean textShadow) {
        return drawText(text,x,y,bg,bgColor,textColor,textShadow,false);
    }

    public static int drawStringWithOutline(String text, int x, int y, int bgColor,int color) {
        int width = getStringWidth(text);
        int height = mc.fontRendererObj.getHeight();
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
        int height = mc.fontRendererObj.getHeight();
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

    public static void color(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f1 = (float) (color >> 16 & 255) / 255.0f;
        float f2 = (float) (color >> 8 & 255) / 255.0f;
        float f3 = (float) (color & 255) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
    }

    public static void enableRender3D(boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }

        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0F);
    }

    public static void disableRender3D(boolean enableDepth) {
        if (enableDepth) {
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
