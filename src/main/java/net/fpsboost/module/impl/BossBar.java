package net.fpsboost.module.impl;

import net.fpsboost.Wrapper;
import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.BossStatus;

import static net.minecraft.client.gui.Gui.icons;
import static net.minecraft.client.gui.Gui.zLevel;

public class BossBar extends Module {
    public static final BossBar INSTANCE = new BossBar();

    public BossBar() {
        super("BossBar", "Boss血条", "Edit the boss bar", "修改boss血条位置");
    }

    static ScaledResolution scaledresolution = new ScaledResolution(mc);
    private final static BooleanValue ishide = new BooleanValue("隐藏怪物血条", "Hide Boss Bar", false);
    private final static BooleanValue move = new BooleanValue("移动位置", "Move Position", false);
    public static int i = (int) scaledresolution.getScaledWidth_double();
    public static int j = 182;
    public static int k = i / 2 - j / 2;
    public static int l = (int) (BossStatus.healthScale * (float) (j + 1));
    public static int i1 = 12;
    private static final NumberValue x = new NumberValue("X坐标", "X Position", i, 1920, 0, 1);
    private static final NumberValue y = new NumberValue("Y坐标", "Y Position", i1, 0, -300, 1);
    private static final BooleanValue moveCenter = new BooleanValue("移动中心", "Move Center", false);

    @Override
    public void onEnable() {
        if (moveCenter.getValue()) {
            moveCenter.setValue(false);
        }
        super.onEnable();
    }

    public void renderBossHealth() {
        if (ishide.getValue()) return;
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime;
            ScaledResolution scaledresolution = new ScaledResolution(Wrapper.mc);
            int i;
            int j = 182;
            int i1;
            if (moveCenter.getValue()) {
                x.setValue(scaledresolution.getScaledWidth_double());
            }
            if (move.getValue()) {
                i1 = -y.getValue().intValue();
                i = x.getValue().intValue();
            } else {
                i = scaledresolution.getScaledWidth();
                i1 = 12;
            }
            int k = i / 2 - j / 2;
//            System.out.println(i);
            int l = (int) (BossStatus.healthScale * (float) (j + 1));
            this.drawTexturedModalRect(k, i1, 0, 74, j, 5);
            this.drawTexturedModalRect(k, i1, 0, 74, j, 5);

            if (l > 0) {
                this.drawTexturedModalRect(k, i1, 0, 79, l, 5);
            }

            String s = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(s, (float) (i / 2 - this.getFontRenderer().getStringWidth(s) / 2), (float) (i1 - 10), 16777215);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Wrapper.mc.getTextureManager().bindTexture(icons);
        }
    }

    public FontRenderer getFontRenderer() {
        return Wrapper.mc.fontRendererObj;
    }

    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, zLevel).tex((float) (textureX) * f, (float) (textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, zLevel).tex((float) (textureX + width) * f, (float) (textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, zLevel).tex((float) (textureX + width) * f, (float) (textureY) * f1).endVertex();
        worldrenderer.pos(x, y, zLevel).tex((float) (textureX) * f, (float) (textureY) * f1).endVertex();
        tessellator.draw();
    }
}
