package net.fpsboost.util;

import net.fpsboost.util.Logger;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.stream.IntStream;

public class IconUtil {

    public static final ResourceLocation icon = new ResourceLocation("client/icons/icon_32x32.png");
    public static final ResourceLocation iconBig = new ResourceLocation("client/icons/icon_150x150.png");

    public static ByteBuffer[] getFavicon() {
        try {
            return new ByteBuffer[] {
                    readImageToBuffer(IconUtil.class.getResourceAsStream("/assets/minecraft/client/icons/icon_16x16.png")),
                    readImageToBuffer(IconUtil.class.getResourceAsStream("/assets/minecraft/client/icons/icon_32x32.png"))
            };
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
        return null;
    }

    private static ByteBuffer readImageToBuffer(final InputStream imageStream) throws IOException {
        if (imageStream == null) {
            return null;
        }

        BufferedImage bufferedImage = ImageIO.read(imageStream);
        int[] rgb = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());

        // 使用 IntStream 代替传统的 for 循环，简化并提高效率
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 * rgb.length);
        IntStream.of(rgb)
                .forEach(i -> byteBuffer.putInt(i << 8 | (i >> 24 & 255))); // 将颜色值处理并存入 ByteBuffer

        byteBuffer.flip();
        return byteBuffer;
    }
}
