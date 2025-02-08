package net.fpsboost.util.screenShot;

import net.fpsboost.Wrapper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import net.minecraft.client.renderer.OpenGlHelper;

import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author LangYa466
 * @since 2025/1/9
 */
public class ScreenshotTaker implements Wrapper {
    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface ScreenshotCallback {
        void onComplete(IChatComponent icc);
        void onError(Exception e);
    }

    public static void takeScreenshotAsync(ScreenshotCallback callback) {
        mc.addScheduledTask(() -> {
            try {
                ScreenshotData screenshotData = captureScreenshot();
                // 异步保存
                executor.submit(() -> {
                    try {
                        IChatComponent result = ScreenshotSaver.saveScreenshotAsync(
                                screenshotData.getWidth(),
                                screenshotData.getHeight(),
                                screenshotData.getPixels(),
                                screenshotData.getFramebuffer()
                        );
                        callback.onComplete(result);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                });
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    private static ScreenshotData captureScreenshot() {
        final Framebuffer framebuffer = mc.getFramebuffer();
        int width = mc.displayWidth;
        int height = mc.displayHeight;

        if (OpenGlHelper.isFramebufferEnabled()) {
            width = framebuffer.framebufferWidth;
            height = framebuffer.framebufferHeight;
        }

        final int size = width * height;

        if (pixelBuffer == null || pixelBuffer.capacity() < size) {
            pixelBuffer = BufferUtils.createIntBuffer(size);
            pixelValues = new int[size];
        }

        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(framebuffer.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
        } else {
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
        }

        pixelBuffer.get(pixelValues);
        TextureUtil.processPixelValues(pixelValues, width, height); // 垂直翻转像素
        for (int i = 0; i < pixelValues.length; i++) {
            int rgba = pixelValues[i];
            // 将 RGBA 转换为 ARGB
            int r = (rgba >> 16) & 0xFF;
            int g = (rgba >> 8) & 0xFF;
            int b = rgba & 0xFF;
            int a = (rgba >> 24) & 0xFF;
            // 重新组合为 ARGB 格式
            pixelValues[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        return new ScreenshotData(width, height, pixelValues.clone(), framebuffer);
    }
}
