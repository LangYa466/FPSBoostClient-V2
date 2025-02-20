package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;

public class PotionDisplay extends Element {

    private final BooleanValue betterFont = new BooleanValue("更好的字体", "BetterFont", true);
    private final BooleanValue backgroundValue = new BooleanValue("背景", "Background", true);
    private final ColorValue color = new ColorValue("背景颜色", "Background Color", new Color(0, 0, 0, 80), this);
    private final ColorValue textColorValue = new ColorValue("药水名字文本颜色", "Potion Name Text Color", Color.white, this);
    private final ColorValue text2ColorValue = new ColorValue("药水时长文本颜色", "Potion Duration Text Color", Color.white, this);
    private final ResourceLocation res = new ResourceLocation("textures/gui/container/inventory.png");

    public PotionDisplay() {
        super("PotionDisplay", "药水显示");
    }

    @Override
    public void onDraw() {
        ArrayList<PotionEffect> collection = new ArrayList<>(mc.thePlayer.getActivePotionEffects());

        FontRenderer fr;
        if (betterFont.getValue()) fr = FontManager.client();
        else fr = mc.fontRendererObj;

        if (collection.isEmpty() && mc.currentScreen instanceof GuiChat) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.example.getId(), -1, -1));
        } else if (!(mc.currentScreen instanceof GuiChat)) {
            mc.thePlayer.removePotionEffect(Potion.example.getId());
        }

        height = (collection.size() * 30);
        if (!collection.isEmpty()) {
            RenderUtil.resetColor();

            // 排序集合，按持续时间和放大器排序
            collection.sort((o1, o2) -> {
                String os1 = getAmplifierString(o1);
                String os2 = getAmplifierString(o2);
                return Integer.compare(fr.getStringWidth(Potion.getDurationString(o2) + os2),
                        fr.getStringWidth(Potion.getDurationString(o1) + os1));
            });

            // 我们定义一个初始 Y 坐标
            int initialY = 0;

            for (int i = 0; i < collection.size(); i++) {
                PotionEffect potioneffect = collection.get(i);

                // 计算当前渲染的 Y 轴位置 - 以递归方式增加
                float posY = initialY - 30;
                for (int j = 0; j <= i; j++) {
                    posY += 30;  // 每个 potion 增加 15 像素的高度
                }

                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                String s1 = I18n.format(potion.getName());
                String s = Potion.getDurationString(potioneffect);

                // 根据放大器添加等级信息
                s1 += getAmplifierString(potioneffect);

                // 计算文本宽度
                int allStringWidth = fr.getStringWidth(s1) + fr.getStringWidth(s);
                if (allStringWidth > width) {
                    width = allStringWidth + 15;
                }

                // 绘制背景矩形
                if (backgroundValue.getValue()) {
                    RenderUtil.drawRect(0, (int) posY, allStringWidth + 13, 25, color.getValueC());
                }

                // 绘制药水名称和持续时间
                fr.drawStringWithShadow(s1, 25, (int) (posY + 3), textColorValue.getValueC());
                fr.drawStringWithShadow(s, 25, (int) (posY + 15), text2ColorValue.getValueC());

                // 绘制状态图标
                if (potion.hasStatusIcon()) {
                    int i1 = potion.getStatusIconIndex();
                    mc.getTextureManager().bindTexture(res);
                    Gui.drawTexturedModalRect2(4, posY + 4.5F, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }
            }
        }
        super.onDraw();
    }

    // 辅助方法，用于获取放大器的字符串表示
    private String getAmplifierString(PotionEffect effect) {
        switch (effect.getAmplifier()) {
            case 1:
                return " " + I18n.format("enchantment.level.2");
            case 2:
                return " " + I18n.format("enchantment.level.3");
            case 3:
                return " " + I18n.format("enchantment.level.4");
            default:
                return "";
        }
    }
}
