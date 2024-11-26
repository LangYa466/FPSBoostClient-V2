package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.ModeValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BlockOverlay extends Module {
    private final BooleanValue mode = new BooleanValue("边框模式","Outline Mode", true);
    private final ColorValue color1 = new ColorValue("边框颜色","Outline Color", new Color(255, 255, 255));
    private final ColorValue color = new ColorValue("填充颜色","Fill Color", new Color(255, 255, 255, 50)) {
        @Override
        public boolean getHasAlpha() {
            return false;
        }
    };
    private final BooleanValue chroma = new BooleanValue("彩虹色","Rainbow", false);
    private final BooleanValue throughBlock = new BooleanValue("边框-立体","3D Outline", true);

    public BlockOverlay() {
        super("BlockOverlay","方块边框","Display the block outline or fill","抄袭FpsMaster的");
    }

    @Override
    public void onRender3D() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final BlockPos pos = mc.objectMouseOver.getBlockPos();
            final Block block = mc.theWorld.getBlockState(pos).getBlock();
            final double n = pos.getX();
            final double x = n - RenderManager.renderPosX;
            final double n2 = pos.getY();
            final double y = n2 - RenderManager.renderPosY;
            final double n3 = pos.getZ();
            final double z = n3 - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(2848);
            if (throughBlock.getValue()) GL11.glDisable(2929);
            GL11.glDepthMask(false);
            int chromaColor = reAlpha(Color.getHSBColor((System.currentTimeMillis() % 3000) / 3000F, 0.8F, 1F).getRGB(), color.getColorC().getAlpha() / 255f);
            Color c = chroma.getValue() ? intToColor(chromaColor) : color1.getColorC();
            GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
            final double minX = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0 : block.getBlockBoundsMinX();
            final double minY = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0 : block.getBlockBoundsMinY();
            final double minZ = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0 : block.getBlockBoundsMinZ();
            if (!mode.getValue()) drawBoundingBox(new AxisAlignedBB(x + minX - 0.005, y + minY - 0.005, z + minZ - 0.005, x + block.getBlockBoundsMaxX() + 0.005, y + block.getBlockBoundsMaxY() + 0.005, z + block.getBlockBoundsMaxZ() + 0.005));
            GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
            GL11.glLineWidth(1f);
            if (mode.getValue()) drawOutlinedBoundingBox(new AxisAlignedBB(x + minX - 0.005, y + minY - 0.005, z + minZ - 0.005, x + block.getBlockBoundsMaxX() + 0.005, y + block.getBlockBoundsMaxY() + 0.005, z + block.getBlockBoundsMaxZ() + 0.005));
            GL11.glDisable(2848);
            GL11.glEnable(3553);
            if (throughBlock.getValue()) {
                GL11.glEnable(2929);
            }
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
    }

    public static int reAlpha(final int color, final float alpha) {
        try {
            final Color c = new Color(color);
            final float r = 0.003921569f * c.getRed();
            final float g = 0.003921569f * c.getGreen();
            final float b = 0.003921569f * c.getBlue();
            return new Color(r, g, b, alpha).getRGB();
        } catch (Throwable e) {
            return color;
        }
    }

    public static Color intToColor(int color) {
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        return new Color(red, green, blue, alpha);
    }

    public static void drawBoundingBox(final AxisAlignedBB aa) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawOutlinedBoundingBox(final AxisAlignedBB aa) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }
}