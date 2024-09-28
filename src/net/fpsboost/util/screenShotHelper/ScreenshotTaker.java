package net.fpsboost.util.screenShotHelper;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import net.minecraft.client.renderer.OpenGlHelper;
import java.nio.IntBuffer;

public class ScreenshotTaker {
    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;
    
    public static String takeScreenshot() {
        final Minecraft client = Minecraft.getMinecraft();
        final Framebuffer func_147110_a = client.getFramebuffer();
        int width = client.displayWidth;
        int height = client.displayHeight;
        if (OpenGlHelper.isFramebufferEnabled()) {
            width = func_147110_a.framebufferWidth;
            height = func_147110_a.framebufferHeight;
        }
        final int n = width * height;
        if (ScreenshotTaker.pixelBuffer == null || ScreenshotTaker.pixelBuffer.capacity() < n) {
            ScreenshotTaker.pixelBuffer = BufferUtils.createIntBuffer(n);
            ScreenshotTaker.pixelValues = new int[n];
        }
        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);
        ScreenshotTaker.pixelBuffer.clear();
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(func_147110_a.framebufferTexture);
            GL11.glGetTexImage(3553, 0, 32993, 33639, ScreenshotTaker.pixelBuffer);
        }
        else {
            GL11.glReadPixels(0, 0, width, height, 32993, 33639, ScreenshotTaker.pixelBuffer);
        }
        ScreenshotTaker.pixelBuffer.get(ScreenshotTaker.pixelValues);
        TextureUtil.processPixelValues(ScreenshotTaker.pixelValues, width, height);
        final int[] pixels = new int[ScreenshotTaker.pixelValues.length];
        System.arraycopy(ScreenshotTaker.pixelValues, 0, pixels, 0, ScreenshotTaker.pixelValues.length);
        return ScreenshotSaver.saveScreenshotAsync(width, height, pixels, func_147110_a);
    }
}
