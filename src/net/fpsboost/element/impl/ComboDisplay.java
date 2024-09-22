package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;

/**
 * @author LangYa
 * @since 2024/8/30 21:23
 */
public class ComboDisplay extends Element {

    public ComboDisplay() {
        super("ComboDisplay", "Combo显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景",true);

    @Override
    public void onDraw() {
        String text = AttackHandler.currentCombo + " Combo";
        width = RenderUtil.drawStringWithRounded(text,0,0,backgroundValue.value);
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.FONT_HEIGHT;
    }
}
