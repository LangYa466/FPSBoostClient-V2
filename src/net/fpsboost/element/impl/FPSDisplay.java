package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.Minecraft;

/**
 * @author LangYa
 * @since 2024/8/30 21:23
 */
public class FPSDisplay extends Element {

    public FPSDisplay() {
        super("FPSDisplay", "FPS显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景",true);
    private final NumberValue backgroundRadiusValue = new NumberValue("背景圆角值", 2D,10D,0D,1D);

    @Override
    public void onDraw() {
        String text = Minecraft.getDebugFPS() + " FPS";
        width = RenderUtil.drawStringWithRounded(text,0,0,backgroundRadiusValue.value.intValue(),backgroundValue.value);
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.FONT_HEIGHT;
    }
}
