package net.fpsboost.util.skinlayers3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

public class SkinUtil
{
    public static boolean hasCustomSkin(final AbstractClientPlayer player) {
        return !DefaultPlayerSkin.getDefaultSkin(player.getUniqueID()).equals((Object)player.getLocationSkin());
    }

    private static NativeImage getSkinTexture(final AbstractClientPlayer player) {
        return getTexture(player.getLocationSkin());
    }

    private static NativeImage getTexture(final ResourceLocation resource) {
        final NativeImage skin = new NativeImage(64, 64, false);
        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        final ITextureObject abstractTexture = textureManager.getTexture(resource);
        if (abstractTexture == null) {
            return null;
        }
        GlStateManager.bindTexture(abstractTexture.getGlTextureId());
        skin.downloadTexture(0, false);
        return skin;
    }

    public static boolean setup3dLayers(final AbstractClientPlayer abstractClientPlayerEntity, final PlayerSettings settings, final boolean thinArms, final ModelPlayer model) {
        if (!hasCustomSkin(abstractClientPlayerEntity)) {
            return false;
        }
        final NativeImage skin = getSkinTexture(abstractClientPlayerEntity);
        if (skin == null) {
            return false;
        }
        final CustomizableModelPart[] layers = { SolidPixelWrapper.wrapBox(skin, 4, 12, 4, 0, 48, true, 0.0f), SolidPixelWrapper.wrapBox(skin, 4, 12, 4, 0, 32, true, 0.0f), null, null, null };
        if (thinArms) {
            layers[2] = SolidPixelWrapper.wrapBox(skin, 3, 12, 4, 48, 48, true, -2.5f);
            layers[3] = SolidPixelWrapper.wrapBox(skin, 3, 12, 4, 40, 32, true, -2.5f);
        }
        else {
            layers[2] = SolidPixelWrapper.wrapBox(skin, 4, 12, 4, 48, 48, true, -2.5f);
            layers[3] = SolidPixelWrapper.wrapBox(skin, 4, 12, 4, 40, 32, true, -2.5f);
        }
        layers[4] = SolidPixelWrapper.wrapBox(skin, 8, 12, 4, 16, 32, true, -0.8f);
        settings.setupSkinLayers(layers);
        settings.setupHeadLayers(SolidPixelWrapper.wrapBox(skin, 8, 8, 8, 32, 0, false, 0.6f));
        skin.close();
        return true;
    }
}
