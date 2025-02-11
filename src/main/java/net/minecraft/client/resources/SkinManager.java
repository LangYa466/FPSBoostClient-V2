package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SkinManager {
    // 线程池，用于异步加载皮肤
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skinCacheLoader;

    public SkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
        this.textureManager = textureManagerInstance;
        this.skinCacheDir = skinCacheDirectory;
        this.sessionService = sessionService;
        this.skinCacheLoader = CacheBuilder.newBuilder()
                .expireAfterAccess(15L, TimeUnit.SECONDS)
                .build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
                    public Map<Type, MinecraftProfileTexture> load(GameProfile profile) throws Exception {
                        return Minecraft.getMinecraft().getSessionService().getTextures(profile, false);
                    }
                });
    }

    public ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, Type type) {
        return this.loadSkin(profileTexture, type, null);
    }

    public ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final Type type, final SkinManager.SkinAvailableCallback skinAvailableCallback) {
        final ResourceLocation resourceLocation = new ResourceLocation("skins/" + profileTexture.getHash());
        ITextureObject textureObject = this.textureManager.getTexture(resourceLocation);

        if (textureObject != null) {
            if (skinAvailableCallback != null) {
                skinAvailableCallback.skinAvailable(type, resourceLocation, profileTexture);
            }
        } else {
            File cacheSubDir = new File(this.skinCacheDir, profileTexture.getHash().length() > 2 ? profileTexture.getHash().substring(0, 2) : "xx");
            File cacheFile = new File(cacheSubDir, profileTexture.getHash());
            final IImageBuffer imageBuffer = type == Type.SKIN ? new ImageBufferDownload() : null;
            ThreadDownloadImageData downloadImageData = new ThreadDownloadImageData(cacheFile, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
                public BufferedImage parseUserSkin(BufferedImage image) {
                    if (imageBuffer != null) {
                        image = imageBuffer.parseUserSkin(image);
                    }
                    return image;
                }

                public void skinAvailable() {
                    if (imageBuffer != null) {
                        imageBuffer.skinAvailable();
                    }
                    if (skinAvailableCallback != null) {
                        skinAvailableCallback.skinAvailable(type, resourceLocation, profileTexture);
                    }
                }
            });
            this.textureManager.loadTexture(resourceLocation, downloadImageData);
        }

        return resourceLocation;
    }

    public void loadProfileTextures(final GameProfile profile, final SkinManager.SkinAvailableCallback skinAvailableCallback, final boolean requireSecure) {
        THREAD_POOL.submit(new Runnable() {
            public void run() {
                final Map<Type, MinecraftProfileTexture> textures = Maps.newHashMap();

                try {
                    textures.putAll(SkinManager.this.sessionService.getTextures(profile, requireSecure));
                } catch (InsecureTextureException e) {
                    // 忽略不安全纹理异常
                }

                if (textures.isEmpty() && profile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
                    profile.getProperties().clear();
                    profile.getProperties().putAll(Minecraft.getMinecraft().getProfileProperties());
                    textures.putAll(SkinManager.this.sessionService.getTextures(profile, false));
                }

                Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                    public void run() {
                        if (textures.containsKey(Type.SKIN)) {
                            SkinManager.this.loadSkin(textures.get(Type.SKIN), Type.SKIN, skinAvailableCallback);
                        }
                        if (textures.containsKey(Type.CAPE)) {
                            SkinManager.this.loadSkin(textures.get(Type.CAPE), Type.CAPE, skinAvailableCallback);
                        }
                    }
                });
            }
        });
    }

    public Map<Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile profile) {
        return this.skinCacheLoader.getUnchecked(profile);
    }

    public interface SkinAvailableCallback {
        void skinAvailable(Type type, ResourceLocation location, MinecraftProfileTexture profileTexture);
    }
}