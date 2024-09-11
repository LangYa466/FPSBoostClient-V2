package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.util.RenderUtil;
import net.minecraft.client.Minecraft;

/**
 * @author LangYa
 * @since 2024/8/30 21:23
 */
public class ComboDisplay extends Element {

    public ComboDisplay() {
        super("ComboDisplay", "Combo显示");
    }

    @Override
    public void onDraw() {
        String text = AttackHandler.currentCombo + " Combo";
        width = RenderUtil.drawStringWithRounded(text,0,0);
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.FONT_HEIGHT;
    }
}
