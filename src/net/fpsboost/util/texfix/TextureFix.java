package net.fpsboost.util.texfix;

import cn.langya.Logger;
import net.fpsboost.Wrapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Map;

// LangYa466
// recode in 2025/1/31
public class TextureFix implements Wrapper {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");
    private static final LinkedList<UnloadEntry> toUnload = new LinkedList<>();

    public static void init() {
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(resourceManager -> {
            TextureMap textureMap = mc.getTextureMapBlocks();
            if (textureMap == null) return;
            addFixList(textureMap.mapRegisteredSprites);
            addFixList(textureMap.mapUploadedSprites);
        });
    }

    public static void markForUnload(TextureAtlasSprite sprite) {
        toUnload.add(new UnloadEntry(sprite));
    }

    public static void onTick() {
        if (!toUnload.isEmpty() && toUnload.getFirst().unload()) {
            toUnload.removeFirst();
        }
    }

    private static long toMB(long bytes) {
        return bytes / 1024L / 1024L;
    }

    private static void addFixList(Map<String, TextureAtlasSprite> spriteMap) {
        long bytes = 0L;
        int fixed = 0;
        for (TextureAtlasSprite sprite : spriteMap.values()) {
            if (!sprite.hasAnimationMetadata()) {
                fixed++;
                bytes += (long) (sprite.getIconWidth() * sprite.getIconHeight() * 4);
                sprite.setFramesTextureData(new FixList(sprite));
            }
        }
        Logger.debug("Fixed Textures: {} Saved: {}MB ({} bytes)", fixed, DECIMAL_FORMAT.format(toMB(bytes)), bytes);
    }

    public static void reloadTextureData(TextureAtlasSprite sprite) {
        reloadTextureData(sprite, mc.getResourceManager());
    }

    private static void reloadTextureData(TextureAtlasSprite sprite, IResourceManager manager) {
        ResourceLocation location = getResourceLocation(sprite);
        if (sprite.hasCustomLoader(manager, location)) {
            sprite.load(manager, location);
        } else {
            try {
                IResource resource = manager.getResource(location);
                BufferedImage[] images = new BufferedImage[1 + mc.gameSettings.mipmapLevels];
                images[0] = TextureUtil.readBufferedImage(resource.getInputStream());
                sprite.loadSprite(images, null);
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
        }
    }

    private static ResourceLocation getResourceLocation(TextureAtlasSprite sprite) {
        ResourceLocation resLoc = new ResourceLocation(sprite.getIconName());
        return new ResourceLocation(resLoc.getResourceDomain(), String.format("textures/%s.png", resLoc.getResourcePath()));
    }

    private static class UnloadEntry {
        private int count = 2;
        private final TextureAtlasSprite sprite;

        public UnloadEntry(TextureAtlasSprite entry) {
            this.sprite = entry;
        }

        public boolean unload() {
            if (--count <= 0) {
                sprite.clearFramesTextureData();
                return true;
            }
            return false;
        }
    }
}
