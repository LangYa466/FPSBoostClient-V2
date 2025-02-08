package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.ColorUtil;
import net.fpsboost.value.impl.ColorValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/11/29 17:35
 */
public class TargetCircle extends Module {
    public TargetCircle() {
        super("TargetCircle", "目标光环", "Add an aura to the person you attack", "给你攻击的人加上光环");
    }

    private final ColorValue colorValue = new ColorValue("颜色", "Color", Color.white, this);

    private double easeInOutQuad(double d) {
        return d < 0.5 ? 2.0 * d * d : 1.0 - Math.pow(-2.0 * d + 2.0, 2.0) / 2.0;
    }

    private static EntityPlayer target;

    public static void onAttack(Entity entity) {
        if (entity instanceof EntityPlayer && !entity.isInvisibleToPlayer(mc.thePlayer)) target = (EntityPlayer) entity;
    }

    @Override
    public void onUpdate() {
        if (target == null) return;
        if (target.getHealth() <= 0 || !mc.thePlayer.canEntityBeSeen(target) || target.worldObj != mc.theWorld) target = null;
        super.onUpdate();
    }

    @Override
    public void onRender3D() {
        if (target == null) return;

        double d = 1500.0;
        double d2 = (double) System.currentTimeMillis() % d;
        boolean bl = d2 > d / 2.0;
        double d3 = d2 / (d / 2.0);
        d3 = !bl ? 1.0 - d3 : d3 - 1.0;
        d3 = this.easeInOutQuad(d3);

        mc.entityRenderer.disableLightmap();

        // Start of OpenGL state setup using GlStateManager
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        // Calculate target position
        double d4 = target.width;
        double d5 = (double) target.height + 0.1;
        double d6 = target.lastTickPosX + (target.posX - target.lastTickPosX) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        double d7 = target.lastTickPosY + (target.posY - target.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY + d5 * d3;
        double d8 = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;
        double d9 = d5 / 3.0 * (d3 > 0.5 ? 1.0 - d3 : d3) * (double) (bl ? -1 : 1);

        // Render circle segments
        for (int i = 0; i < 360; i += 5) {
            Color color = colorValue.getValue().getColor();
            double d10 = d6 - Math.sin((double) i * Math.PI / 180.0) * d4;
            double d11 = d8 + Math.cos((double) i * Math.PI / 180.0) * d4;
            double d12 = d6 - Math.sin((double) (i - 5) * Math.PI / 180.0) * d4;
            double d13 = d8 + Math.cos((double) (i - 5) * Math.PI / 180.0) * d4;
            int blue = ColorUtil.pulseColor(color, 200, 1).getBlue();
            int green = ColorUtil.pulseColor(color, 200, 1).getGreen();
            int red = ColorUtil.pulseColor(color, 200, 1).getRed();
            // 十六进制
            float rgbaAlpha = 255.0f;
            // Render the filled segments using GL11
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f((float) red / rgbaAlpha,
                    (float) green / rgbaAlpha,
                    (float) blue / rgbaAlpha, 0.0f);
            GL11.glVertex3d(d10, d7 + d9, d11);
            GL11.glVertex3d(d12, d7 + d9, d13);

            GL11.glColor4f((float) red / rgbaAlpha,
                    (float) green / rgbaAlpha,
                    (float) blue / rgbaAlpha, 200.0f);
            GL11.glVertex3d(d12, d7, d13);
            GL11.glVertex3d(d10, d7, d11);
            GL11.glEnd();

            // Render the outline using GlStateManager
            GlStateManager.color((float) red / rgbaAlpha,
                    (float) green / rgbaAlpha,
                    (float) blue / rgbaAlpha, 1.0f);
            GlStateManager.glBegin(GL11.GL_LINES);
            GL11.glVertex3d(d12, d7, d13);
            GL11.glVertex3d(d10, d7, d11);
            GlStateManager.glEnd();
        }

        // Restore OpenGL states using GlStateManager
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
