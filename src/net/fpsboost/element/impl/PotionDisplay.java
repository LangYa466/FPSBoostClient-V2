package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import static net.fpsboost.util.ThemeUtil.bgColor;

public class PotionDisplay extends Element {

    private final BooleanValue backgroundValue = new BooleanValue("背景",true);
    private final ColorValue color = new ColorValue("背景颜色",-1);

    private final ResourceLocation res = new ResourceLocation("textures/gui/container/inventory.png");

    public PotionDisplay() {
        super("PotionDisplay", "药水显示");
    }

    @Override
    public void onDraw() {
        ArrayList<PotionEffect> collection = new ArrayList<>(mc.thePlayer.getActivePotionEffects());

        height = (collection.size() * 30);
        if (!collection.isEmpty()) {
            RenderUtil.resetColor();
            collection.sort((o1, o2) -> {
                String os1 = "";

                if (o1.getAmplifier() == 1) {
                    os1 = os1 + " " + I18n.format("enchantment.level.2");
                } else if (o1.getAmplifier() == 2) {
                    os1 = os1 + " " + I18n.format("enchantment.level.3");
                } else if (o1.getAmplifier() == 3) {
                    os1 = os1 + " " + I18n.format("enchantment.level.4");
                }

                String os2 = "";

                if (o2.getAmplifier() == 1) {
                    os2 = os2 + " " + I18n.format("enchantment.level.2");
                } else if (o2.getAmplifier() == 2) {
                    os2 = os2 + " " + I18n.format("enchantment.level.3");
                } else if (o2.getAmplifier() == 3) {
                    os2 = os2 + " " + I18n.format("enchantment.level.4");
                }
                return Integer.compare(RenderUtil.getStringWidth(Potion.getDurationString(o2) + os2), RenderUtil.getStringWidth(Potion.getDurationString(o1) + os1));
            });

            int count = 0;
            for (PotionEffect potioneffect : collection) {
                count++;
                GlStateManager.pushMatrix();
                if (count > 1) GL11.glTranslatef(0F,count * 15F,0F);
                int allStringWidth;
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];

                String s1 = I18n.format(potion.getName());
                String s = Potion.getDurationString(potioneffect);

                if (potioneffect.getAmplifier() == 1) {
                    s1 = s1 + " " + I18n.format("enchantment.level.2");
                } else if (potioneffect.getAmplifier() == 2) {
                    s1 = s1 + " " + I18n.format("enchantment.level.3");
                } else if (potioneffect.getAmplifier() == 3) {
                    s1 = s1 + " " + I18n.format("enchantment.level.4");
                }

                allStringWidth = RenderUtil.getStringWidth(s1) + RenderUtil.getStringWidth(s);
                if (allStringWidth > width) {
                    width = allStringWidth + 13;
                }

                if (backgroundValue.getValue()) RenderUtil.drawRect(0,0,allStringWidth + 13,25,color.getColor());

                // draw potion name with i18n
                RenderUtil.drawStringWithShadow(s1, 25,3, -1);
                // draw potion duration
                RenderUtil.drawStringWithShadow(s, 25, 15, -1);

                RenderUtil.resetColor();
                if (potion.hasStatusIcon()) {
                    int i1 = potion.getStatusIconIndex();
                    mc.getTextureManager().bindTexture(res);
                    Gui.drawTexturedModalRect2(4,4.5F, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }
                GlStateManager.popMatrix();
            }
        }
        super.onDraw();
    }
}
