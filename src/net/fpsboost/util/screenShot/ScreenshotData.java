package net.fpsboost.util.screenShot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.shader.Framebuffer;

/**
 * @author LangYa466
 * @since 2025/1/9
 */
@AllArgsConstructor
@Getter
@Setter
public class ScreenshotData {
    private final int width, height;
    private final int[] pixels;
    private final Framebuffer framebuffer;
}
