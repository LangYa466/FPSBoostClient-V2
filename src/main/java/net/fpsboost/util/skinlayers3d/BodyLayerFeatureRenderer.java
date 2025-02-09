package net.fpsboost.util.skinlayers3d;

import net.fpsboost.module.impl.SkinLayers3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BodyLayerFeatureRenderer implements LayerRenderer<AbstractClientPlayer>
{
    private final boolean thinArms;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final List<Layer> bodyLayers;

    public BodyLayerFeatureRenderer(RenderPlayer playerRenderer) {
        this.bodyLayers = new ArrayList<>();
        this.thinArms = ((PlayerEntityModelAccessor)playerRenderer).hasThinArms();
        this.bodyLayers.add(new Layer(0, false, EnumPlayerModelParts.LEFT_PANTS_LEG, Shape.LEGS, () -> playerRenderer.getMainModel().bipedLeftLeg));
        this.bodyLayers.add(new Layer(1, false, EnumPlayerModelParts.RIGHT_PANTS_LEG, Shape.LEGS, () -> playerRenderer.getMainModel().bipedRightLeg));
        this.bodyLayers.add(new Layer(2, false, EnumPlayerModelParts.LEFT_SLEEVE, this.thinArms ? Shape.ARMS_SLIM : Shape.ARMS, () -> playerRenderer.getMainModel().bipedLeftArm));
        this.bodyLayers.add(new Layer(3, true, EnumPlayerModelParts.RIGHT_SLEEVE, this.thinArms ? Shape.ARMS_SLIM : Shape.ARMS, () -> playerRenderer.getMainModel().bipedRightArm));
        this.bodyLayers.add(new Layer(4, false, EnumPlayerModelParts.JACKET, Shape.BODY, () -> playerRenderer.getMainModel().bipedBody));
    }

    public void doRenderLayer(AbstractClientPlayer player, float nameFloat1, float nameFloat2, float nameFloat3, float partialTicks, float nameFloat5, float nameFloat6, float nameFloat7) {
        if (!player.hasSkin() || player.isInvisible()) {
            return;
        }
        if (BodyLayerFeatureRenderer.mc.theWorld == null) {
            return;
        }
        if (BodyLayerFeatureRenderer.mc.thePlayer.getPositionVector().squareDistanceTo(player.getPositionVector()) >
                SkinLayers3D.renderDistance.getValue().intValue() * SkinLayers3D.renderDistance.getValue().intValue()) {
            return;
        }
        if (((PlayerSettings) player).getSkinLayers() == null && !this.setupModel(player, (PlayerSettings)player)) {
            return;
        }
        this.renderLayers(player, ((PlayerSettings) player).getSkinLayers());
    }

    private boolean setupModel(AbstractClientPlayer abstractClientPlayerEntity, PlayerSettings settings) {
        if (!SkinUtil.hasCustomSkin(abstractClientPlayerEntity)) {
            return false;
        }
        SkinUtil.setup3dLayers(abstractClientPlayerEntity, settings, this.thinArms, null);
        return true;
    }

    public void renderLayers(AbstractClientPlayer abstractClientPlayer, CustomizableModelPart[] layers) {
        if (layers == null) {
            return;
        }
        float pixelScaling = SkinLayers3D.baseVoxelSize.getValue().floatValue();
        float heightScaling = 1.035f;
        float widthScaling;
        boolean redTint = abstractClientPlayer.hurtTime > 0 || abstractClientPlayer.deathTime > 0;
        for (final Layer layer : this.bodyLayers) {
            if (abstractClientPlayer.isWearing(layer.modelPart) && !layer.vanillaGetter.get().isHidden) {
                GlStateManager.pushMatrix();
                if (abstractClientPlayer.isSneaking()) {
                    GlStateManager.translate(0.0f, 0.2f, 0.0f);
                }
                layer.vanillaGetter.get().postRender(0.0625f);
                if (layer.shape == Shape.ARMS) {
                    layers[layer.layersId].x = 15.968f;
                }
                else if (layer.shape == Shape.ARMS_SLIM) {
                    layers[layer.layersId].x = 7.984f;
                }
                if (layer.shape == Shape.BODY) {
                    widthScaling = SkinLayers3D.bodyVoxelSize.getValue().floatValue();
                }
                else {
                    widthScaling = SkinLayers3D.baseVoxelSize.getValue().floatValue();
                }
                if (layer.mirrored) {
                    final CustomizableModelPart customizableModelPart = layers[layer.layersId];
                    customizableModelPart.x *= -1.0f;
                }
                GlStateManager.scale(0.0625, 0.0625, 0.0625);
                GlStateManager.scale(widthScaling, heightScaling, pixelScaling);
                layers[layer.layersId].y = layer.shape.yOffsetMagicValue;
                layers[layer.layersId].render(redTint);
                GlStateManager.popMatrix();
            }
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }

    static class Layer
    {
        public final int layersId;
        public final boolean mirrored;
        public final EnumPlayerModelParts modelPart;
        public final Shape shape;
        public final Supplier<ModelRenderer> vanillaGetter;

        public Layer(int layersId, boolean mirrored, EnumPlayerModelParts modelPart, Shape shape, Supplier<ModelRenderer> vanillaGetter) {
            this.layersId = layersId;
            this.mirrored = mirrored;
            this.modelPart = modelPart;
            this.shape = shape;
            this.vanillaGetter = vanillaGetter;
        }
    }

    private enum Shape {
        HEAD(0.0f),
        BODY(0.6f),
        LEGS(-0.2f),
        ARMS(0.4f),
        ARMS_SLIM(0.4f);

        private final float yOffsetMagicValue;

        Shape(float yOffsetMagicValue) {
            this.yOffsetMagicValue = yOffsetMagicValue;
        }
    }
}
