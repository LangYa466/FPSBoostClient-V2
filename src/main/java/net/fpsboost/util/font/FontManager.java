package net.fpsboost.util.font;

import net.fpsboost.util.Logger;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.util.font.impl.RapeMasterFontManager;
import net.fpsboost.util.font.impl.UFontRenderer;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

/**
 @author LangYa466
 **/
public class FontManager {
    private static final HashMap<Integer, FontRenderer> fontMap = new HashMap<>();
    private static final HashMap<String, FontRenderer> iconFontMap = new HashMap<>();

    // 默认字体
    private static final String fontName = "汉仪雅黑";
    private static final File fontFile = new File(ConfigManager.dir,"client.ttf");

    public static void init() {
        InputStream is = FontManager.class.getResourceAsStream("/assets/minecraft/client/fonts/default.ttf");

        // 确保文件存在
        if (!fontFile.exists()) {
            try {
                // 如果文件不存在，创建文件
                if (!fontFile.createNewFile()) {
                    Logger.error("Failed to create font file: " + fontFile.getAbsolutePath());
                    return;
                }
            } catch (IOException e) {
                Logger.error(e.getMessage());
                return;
            }
        }

        // 将 InputStream 内容写入文件
        try (OutputStream os = Files.newOutputStream(fontFile.toPath())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is != null ? is.read(buffer) : 0) != -1) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
        } finally {
            try {
                assert is != null;
                is.close();
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        }
    }

    public static Font getFont(int size) {
        Font font;
        try {
            InputStream is = Files.newInputStream(fontFile.toPath());
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            Logger.error("Failed to load font: " + fontName + ", using default font.\n Error: {}",ex.getMessage());
            font = new Font("Arial", Font.PLAIN, size); // 使用默认字体作为后备
        }
        return font;
    }

    public static Font getFont(String name, int size) {
        Font font;
        try {
            InputStream is = FontManager.class.getResourceAsStream("/assets/minecraft/client/fonts/" + name + ".ttf");
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            Logger.error("Failed to load font: " + fontName + ", using default font. Error: {}",ex.getMessage());
            font = new Font("Arial", Font.PLAIN, size);
        }
        return font;
    }

    private static FontRenderer getRenderer(int size) {
        if (fontMap.containsKey(size)) return fontMap.get(size);
        FontRenderer newRenderer = new UFontRenderer(getFont(size), size);
        fontMap.put(size, newRenderer);
        return newRenderer;
    }

    private static FontRenderer getRenderer2(String name,int size) {
        String key = String.format("%s-%s",name,size);
        if (iconFontMap.containsKey(key)) return iconFontMap.get(key);
        FontRenderer newRenderer = new RapeMasterFontManager(getFont(name,size),size);
        iconFontMap.put(key, newRenderer);
        return newRenderer;
    }

    public static FontRenderer client() {
        // mc默认字体大小
        return client(17);
    }

    public static FontRenderer client(int size) {
        return getRenderer(size);
    }

    public static FontRenderer logo(int size) {
        return getRenderer2("logo",size);
    }

    public static FontRenderer arial(int size) {
        return getRenderer2("arial",size);
    }

    public static FontRenderer arialBold(int size) {
        return getRenderer2("arial",size);
    }
}
