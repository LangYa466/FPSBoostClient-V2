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


    public static void drawRoundedRect(int x, int y, int width, int height, int cornerRadius, Color color) {
        Gui.drawRect(x, y + cornerRadius, x + cornerRadius, y + height - cornerRadius, color.getRGB());
        Gui.drawRect(x + cornerRadius, y, x + width - cornerRadius, y + height, color.getRGB());
        Gui.drawRect(x + width - cornerRadius, y + cornerRadius, x + width, y + height - cornerRadius, color.getRGB());

        drawArc(x + cornerRadius, y + cornerRadius, cornerRadius, 0, 90, color);
        drawArc(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270, 360, color);
        drawArc(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 180, 270, color);
        drawArc(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90, 180, color);
    }

    public static void drawRoundedRect(int x, int y, int width, int height, int cornerRadius, int color) {
        drawRoundedRect(x,y,width,height,cornerRadius,new Color(color));
    }

    public static void drawArc(int x, int y, int radius, int startAngle, int endAngle, Color color) {

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255);

        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

        worldRenderer.begin(6, DefaultVertexFormats.POSITION);
        worldRenderer.pos(x, y, 0).endVertex();

        for (int i = (int) (startAngle / 360.0 * 100); i <= (int) (endAngle / 360.0 * 100); i++) {
            double angle = (Math.PI * 2 * i / 100) + Math.toRadians(180);
            worldRenderer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).endVertex();
        }

        Tessellator.getInstance().draw();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
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
        return drawStringWithRounded(text,x,y,2,true);
    }

    public static int drawStringWithRounded(String text, int x, int y,int radius, boolean bg) {
        int width = mc.fontRendererObj.getStringWidth(text);
        int height = mc.fontRendererObj.FONT_HEIGHT;
        if (bg) RenderUtil.drawRoundedRect(x, y, width + 4,height,radius,bgColor);
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
        RenderUtil.drawRoundedRect(x, y, width,height,2,pressbgColor);
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
