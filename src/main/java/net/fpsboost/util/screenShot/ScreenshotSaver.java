package net.fpsboost.util.screenShot;

import net.fpsboost.util.Logger;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LangYa466
 * @since 2025/1/9
 */
public class ScreenshotSaver {
    public static IChatComponent saveScreenshotAsync(final int width, final int height, final int[] pixels, final Framebuffer frameBuffer) {
        BufferedImage bufferedImage;
        if (OpenGlHelper.isFramebufferEnabled()) {
            bufferedImage = new BufferedImage(frameBuffer.framebufferWidth, frameBuffer.framebufferHeight, BufferedImage.TYPE_INT_ARGB);
            int offset = frameBuffer.framebufferTextureHeight - frameBuffer.framebufferHeight;
            for (int i = offset; i < frameBuffer.framebufferTextureHeight; ++i) {
                for (int j = 0; j < frameBuffer.framebufferWidth; ++j) {
                    int index = i * frameBuffer.framebufferTextureWidth + j;
                    int rgba = pixels[index];
                    int argb = ((rgba & 0xFF) << 16) | (rgba & 0xFF00) | ((rgba >> 16) & 0xFF) | (rgba & 0xFF000000);
                    bufferedImage.setRGB(j, i - offset, argb);
                }
            }
        } else {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    int index = i * width + j;
                    int rgba = pixels[index];
                    int argb = ((rgba & 0xFF) << 16) | (rgba & 0xFF00) | ((rgba >> 16) & 0xFF) | (rgba & 0xFF000000);
                    bufferedImage.setRGB(j, i, argb);
                }
            }
        }

        File screenshotDir = new File("screenshots");
        if (!screenshotDir.exists() && !screenshotDir.mkdirs()) {
            return new ChatComponentTranslation("screenshot.failure", "Failed to create screenshot directory");
        }

        String captureTime = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        File screenshotFile = new File(screenshotDir, captureTime + ".png");
        try {
            ImageIO.write(bufferedImage, "png", screenshotFile);
            return new ChatComponentText(EnumChatFormatting.UNDERLINE + "保存截图" + EnumChatFormatting.RESET + " ")
                    .appendSibling(new ChatComponentText("[打开] ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".screenshot open " + screenshotFile.getName())))
                            .appendSibling(new ChatComponentText("[复制] ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".screenshot copy " + screenshotFile.getName())))
                                    .appendSibling(new ChatComponentText("[删除]").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".screenshot del " + screenshotFile.getName()))))));
        } catch (Exception e) {
            Logger.error("Failed to save screenshot: " + e.getMessage());
            return new ChatComponentTranslation("screenshot.failure", e.getMessage());
        }

    }
}
