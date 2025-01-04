package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.Minecraft;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/8/30 21:23
 */
public class FPSDisplay extends Element {

    public FPSDisplay() {
        super("FPSDisplay", "FPS显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景","background",true);
    private final BooleanValue textShadowValue = new BooleanValue("字体阴影","Text Shadow",true);
    private final BooleanValue clientFontValue = new BooleanValue("更好的字体","Better Font",true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色","Background Color",new Color(0,0,0,80),this);
    private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color",Color.white,this);

    @Override
    public void onDraw() {
        String text = Minecraft.getDebugFPS() + " FPS";
        width = RenderUtil.drawText(text,0,0,backgroundValue.getValue(),bgColorValue.getValueC(),textColorValue.getValueC(),textShadowValue.getValue(),clientFontValue.getValue());
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.getHeight();
    }
}
