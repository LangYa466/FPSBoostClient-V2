package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Gui
{
    public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    public static float zLevel;

    public void drawHorizontalLine(int startX, int endX, int y, int color)
    {
        if (endX < startX)
        {
            int i = startX;
            startX = endX;
            endX = i;
        }

        drawRect(startX, y, endX + 1, y + 1, color);
    }

    public void drawVerticalLine(int x, int startY, int endY, int color)
    {
        if (endY < startY)
        {
            int i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    // 通用的绘制矩形方法
    private static void drawRectCommon(double left, double top, double right, double bottom, float f, float f1, float f2, float f3) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        // 开始绘制
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
    }

    // 绘制矩形（浮动坐标）
    public static void drawRect(double left, double top, double right, double bottom, int color) {
        // 调整坐标，确保 left <= right，top <= bottom
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        // 解析颜色
        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        // 设置 OpenGL 状态
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);

        // 调用通用的矩形绘制方法
        drawRectCommon(left, top, right, bottom, f, f1, f2, f3);

        // 恢复 OpenGL 状态
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    // 绘制矩形（整数坐标）
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        // 调整坐标，确保 left <= right，top <= bottom
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        // 解析颜色
        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        // 设置 OpenGL 状态
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);

        // 调用通用的矩形绘制方法
        drawRectCommon(left, top, right, bottom, f, f1, f2, f3);

        // 恢复 OpenGL 状态
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color, true);
    }

    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)x, (float)y, color, true);
    }

    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, zLevel).tex((float)(textureX) * f, (float)(textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, zLevel).tex((float)(textureX + width) * f, (float)(textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, zLevel).tex((float)(textureX + width) * f, (float)(textureY) * f1).endVertex();
        worldrenderer.pos(x, y, zLevel).tex((float)(textureX) * f, (float)(textureY) * f1).endVertex();
        tessellator.draw();
    }

    public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(xCoord + 0.0F, yCoord + (float)maxV, zLevel).tex((float)(minU) * f, (float)(minV + maxV) * f1).endVertex();
        worldrenderer.pos(xCoord + (float)maxU, yCoord + (float)maxV, zLevel).tex((float)(minU + maxU) * f, (float)(minV + maxV) * f1).endVertex();
        worldrenderer.pos(xCoord + (float)maxU, yCoord + 0.0F, zLevel).tex((float)(minU + maxU) * f, (float)(minV) * f1).endVertex();
        worldrenderer.pos(xCoord + 0.0F, yCoord + 0.0F, zLevel).tex((float)(minU) * f, (float)(minV) * f1).endVertex();
        tessellator.draw();
    }

    private static final float TEX_SCALE = 0.00390625F; // 1/256

    public static void drawTexturedModalRect2(float x, float y, int textureX, int textureY, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        // 使用局部变量缓存 tex 坐标计算
        float texX1 = textureX * TEX_SCALE;
        float texY1 = textureY * TEX_SCALE;
        float texX2 = (textureX + width) * TEX_SCALE;
        float texY2 = (textureY + height) * TEX_SCALE;

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, zLevel).tex(texX1, texY2).endVertex();
        worldrenderer.pos(x + width, y + height, zLevel).tex(texX2, texY2).endVertex();
        worldrenderer.pos(x + width, y, zLevel).tex(texX2, texY1).endVertex();
        worldrenderer.pos(x, y, zLevel).tex(texX1, texY1).endVertex();
        tessellator.draw();
    }
    public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        // 使用局部变量缓存 tex 坐标计算
        float minU = textureSprite.getMinU();
        float minV = textureSprite.getMinV();
        float maxU = textureSprite.getMaxU();
        float maxV = textureSprite.getMaxV();

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(xCoord, yCoord + heightIn, zLevel).tex(minU, maxV).endVertex();
        worldrenderer.pos(xCoord + widthIn, yCoord + heightIn, zLevel).tex(maxU, maxV).endVertex();
        worldrenderer.pos(xCoord + widthIn, yCoord, zLevel).tex(maxU, minV).endVertex();
        worldrenderer.pos(xCoord, yCoord, zLevel).tex(minU, minV).endVertex();
        tessellator.draw();
    }

    public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        // 使用局部变量缓存 tex 坐标计算
        float texU1 = u * f;
        float texV1 = v * f1;
        float texU2 = (u + width) * f;
        float texV2 = (v + height) * f1;

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(texU1, texV2).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex(texU2, texV2).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex(texU2, texV1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(texU1, texV1).endVertex();
        tessellator.draw();
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        // 使用局部变量缓存 tex 坐标计算
        float texU1 = u * f;
        float texV1 = v * f1;
        float texU2 = (u + uWidth) * f;
        float texV2 = (v + vHeight) * f1;

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(texU1, texV2).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex(texU2, texV2).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex(texU2, texV1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(texU1, texV1).endVertex();
        tessellator.draw();
    }
}
