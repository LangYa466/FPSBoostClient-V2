package net.fpsboost.util.skinlayers3d;

import com.google.common.collect.Sets;
import net.fpsboost.module.impl.SkinLayers3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class HeadLayerFeatureRenderer implements LayerRenderer<AbstractClientPlayer> {
    private final Set<Item> hideHeadLayers;
    private final boolean thinArms;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final RenderPlayer playerRenderer;

    public HeadLayerFeatureRenderer(RenderPlayer playerRenderer) {
        this.hideHeadLayers = Sets.newHashSet(Items.skull);
        this.thinArms = ((PlayerEntityModelAccessor) playerRenderer).hasThinArms();
        this.playerRenderer = playerRenderer;
    }

    public void doRenderLayer(AbstractClientPlayer player, float nameFloat1, float nameFloat2, float nameFloat3, float partialTicks, float nameFloat5, float nameFloat6, float nameFloat7) {
        if (!player.hasSkin() || player.isInvisible() || !player.isWearing(EnumPlayerModelParts.HAT)) {
            return;
        }
        if (HeadLayerFeatureRenderer.mc.thePlayer.getPositionVector().squareDistanceTo(player.getPositionVector()) >
                SkinLayers3D.renderDistance.getValue().intValue() * SkinLayers3D.renderDistance.minValue) {
            return;
        }
        final ItemStack itemStack = player.getEquipmentInSlot(1);
        if (itemStack != null && this.hideHeadLayers.contains(itemStack.getItem())) {
            return;
        }
        if (((PlayerSettings) player).getHeadLayers() == null && !this.setupModel(player, (PlayerSettings) player)) {
            return;
        }
        this.renderCustomHelmet((PlayerSettings) player, player);
    }

    public void doRenderLayer(AbstractClientPlayer player) {
        if (!player.hasSkin() || player.isInvisible() || !player.isWearing(EnumPlayerModelParts.HAT)) {
            return;
        }
        if (HeadLayerFeatureRenderer.mc.thePlayer.getPositionVector().squareDistanceTo(player.getPositionVector()) >
                SkinLayers3D.renderDistance.getValue() * SkinLayers3D.renderDistance.getValue()) {
            return;
        }
        final ItemStack itemStack = player.getEquipmentInSlot(1);
        if (itemStack != null && this.hideHeadLayers.contains(itemStack.getItem())) {
            return;
        }
        if (((PlayerSettings) player).getHeadLayers() == null && !this.setupModel(player, (PlayerSettings) player)) {
            return;
        }
        this.renderCustomHelmet((PlayerSettings) player, player);
    }

    private boolean setupModel(final AbstractClientPlayer abstractClientPlayerEntity, final PlayerSettings settings) {
        if (!SkinUtil.hasCustomSkin(abstractClientPlayerEntity)) {
            return false;
        }
        SkinUtil.setup3dLayers(abstractClientPlayerEntity, settings, this.thinArms, null);
        return true;
    }

    public void renderCustomHelmet(final PlayerSettings settings, final AbstractClientPlayer abstractClientPlayer) {
        if (settings.getHeadLayers() == null) {
            return;
        }
        if (this.playerRenderer.getMainModel().bipedHead.isHidden) {
            return;
        }
        float voxelSize = SkinLayers3D.headVoxelSize.getValue().floatValue();
        GlStateManager.pushMatrix();
        if (abstractClientPlayer.isSneaking()) {
            GlStateManager.translate(0.0f, 0.2f, 0.0f);
        }
        this.playerRenderer.getMainModel().bipedHead.postRender(0.0625f);
        GlStateManager.scale(0.0625, 0.0625, 0.0625);
        GlStateManager.scale(voxelSize, voxelSize, voxelSize);
        boolean tintRed = abstractClientPlayer.hurtTime > 0 || abstractClientPlayer.deathTime > 0;
        settings.getHeadLayers().render(tintRed);
        GlStateManager.popMatrix();
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
