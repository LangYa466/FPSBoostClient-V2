package net.fpsboost.util.screenShotHelper;

import java.io.IOException;

import net.minecraft.client.resources.I18n;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.OpenGlHelper;
import java.util.Date;
import java.text.SimpleDateFormat;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ScreenshotSaver implements Runnable {
    private int width;
    private int height;
    private String captureTime;
    private int[] pixels;
    private Framebuffer frameBuffer;
    private IChatComponent icc;
    
    public static IChatComponent saveScreenshotAsync(final int width, final int height, final int[] pixels, final Framebuffer frameBuffer) {
        final ScreenshotSaver screenshotSaver = new ScreenshotSaver();
        screenshotSaver.width = width;
        screenshotSaver.height = height;
        screenshotSaver.captureTime = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        screenshotSaver.pixels = pixels;
        screenshotSaver.frameBuffer = frameBuffer;
        // 千万别改成start 改成start synchronized的阻塞会导致空指针然后崩溃
        new Thread(screenshotSaver).run();
        return screenshotSaver.icc;
    }
    
    @Override
    public void run() {
        BufferedImage bufferedImage;
        if (OpenGlHelper.isFramebufferEnabled()) {
            bufferedImage = new BufferedImage(this.frameBuffer.framebufferWidth, this.frameBuffer.framebufferHeight, 1);
            int i;
            for (int n = i = this.frameBuffer.framebufferTextureHeight - this.frameBuffer.framebufferHeight; i < this.frameBuffer.framebufferTextureHeight; ++i) {
                for (int j = 0; j < this.frameBuffer.framebufferWidth; ++j) {
                    bufferedImage.setRGB(j, i - n, this.pixels[i * this.frameBuffer.framebufferTextureWidth + j]);
                }
            }
        }
        else {
            bufferedImage = new BufferedImage(this.width, this.height, 1);
            bufferedImage.setRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);
        }
        final File file = new File("screenshots");
        File ssFile = new File("screenshots", this.captureTime + ".png");
        for (int n2 = 0; ssFile.exists(); ssFile = new File("screenshots", this.captureTime + "_" + n2 + ".png")) {
            ++n2;
        }
        try {
            file.mkdirs();
            ImageIO.write(bufferedImage, "png", ssFile);
            IChatComponent ichatcomponent = new ChatComponentText(ssFile.getName());
            ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, ssFile.getAbsolutePath()));
            ichatcomponent.getChatStyle().setUnderlined(true);
            icc = new ChatComponentTranslation("screenshot.success", ichatcomponent);
        }
        catch (final IOException ex) {
            icc = new ChatComponentTranslation("screenshot.failure", ex.getMessage());;
        }
    }
}
