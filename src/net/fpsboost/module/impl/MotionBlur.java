package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.src.Config;
import org.lwjgl.opengl.GL11;

// https://github.com/FPSMasterTeam/FPSMaster/blob/master/shared/java/top/fpsmaster/features/impl/render/MotionBlur.kt
public class MotionBlur extends Module {
    public static final MotionBlur INSTANCE = new MotionBlur();

    private Framebuffer blurBufferMain;
    private Framebuffer blurBufferInto;
    private final NumberValue multiplier = new NumberValue("Multiplier", "模糊度数" , 2, 10, 0, 0.5F);

    public MotionBlur() {
        super("MotionBlur", "动态模糊");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (Config.isFastRender()) mc.gameSettings.ofFastRender = false;
    }

    private Framebuffer checkFrameBufferSizes(Framebuffer framebuffer, int width, int height) {
        if (framebuffer == null || framebuffer.framebufferWidth != width || framebuffer.framebufferHeight != height) {
            if (framebuffer == null) {
                framebuffer = new Framebuffer(width, height, true);
            } else {
                framebuffer.createBindFramebuffer(width, height);
            }
            framebuffer.setFramebufferFilter(9728);
        }
        return framebuffer;
    }

    private void drawTexturedRectNoBlend(float x, float y, float width, float height, float vMin, float vMax) {
        GlStateManager.enableTexture2D();
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0).tex((float) 0.0, vMax).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0).tex((float) 1.0, vMax).endVertex();
        worldrenderer.pos(x + width, y, 0.0).tex((float) 1.0, vMin).endVertex();
        worldrenderer.pos(x, y, 0.0).tex((float) 0.0, vMin).endVertex();
        tessellator.draw();
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
    }

    public void renderOverlay() {
        if (!enable) return;
        if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 20) return;
        if (mc.currentScreen == null) {
            if (OpenGlHelper.isFramebufferEnabled()) {
                ScaledResolution sr = new ScaledResolution(mc);
                int width = mc.getFramebuffer().framebufferWidth;
                int height = mc.getFramebuffer().framebufferHeight;

                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0, width / (double) sr.getScaleFactor(), height / (double) sr.getScaleFactor(), 0.0, 2000.0, 4000.0);
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                GlStateManager.translate(0f, 0f, -2000f);

                blurBufferMain = checkFrameBufferSizes(blurBufferMain, width, height);
                blurBufferInto = checkFrameBufferSizes(blurBufferInto, width, height);

                blurBufferInto.framebufferClear();
                blurBufferInto.bindFramebuffer(true);

                OpenGlHelper.glBlendFunc(770, 771, 0, 1);
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                GlStateManager.disableBlend();
                mc.getFramebuffer().bindFramebufferTexture();

                GlStateManager.color(1f, 1f, 1f, 1f);
                drawTexturedRectNoBlend(0.0f, 0.0f, width / (float) sr.getScaleFactor(), height / (float) sr.getScaleFactor(), 0.0f, 1.0f);

                GlStateManager.enableBlend();
                blurBufferMain.bindFramebufferTexture();
                GlStateManager.color(1f, 1f, 1f, (float) (multiplier.getValue() / 10 - 0.1));
                drawTexturedRectNoBlend(0f, 0f, width / (float) sr.getScaleFactor(), height / (float) sr.getScaleFactor(), 1f, 0f);

                mc.getFramebuffer().bindFramebuffer(true);
                blurBufferInto.bindFramebufferTexture();
                GlStateManager.color(1f, 1f, 1f, 1f);
                GlStateManager.enableBlend();
                OpenGlHelper.glBlendFunc(770, 771, 1, 771);
                drawTexturedRectNoBlend(0.0f, 0.0f, width / (float) sr.getScaleFactor(), height / (float) sr.getScaleFactor(), 0.0f, 1.0f);

                Framebuffer tempBuff = blurBufferMain;
                blurBufferMain = blurBufferInto;
                blurBufferInto = tempBuff;
            }
        }
    }
}
