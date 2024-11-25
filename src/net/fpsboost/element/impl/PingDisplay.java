package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/8/30 21:23
 */
public class PingDisplay extends Element {

    public PingDisplay() {
        super("PingDisplay", "Ping显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景",true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色",new Color(0,0,0,80));
    private final ColorValue textColorValue = new ColorValue("文本颜色",Color.white);

    @Override
    public void onDraw() {
        String text;

        if (mc.getCurrentServerData() != null) {
            text = String.format("%sms",((int) mc.getCurrentServerData().pingToServer));
        } else {
            text = "0ms";
        }
        width = RenderUtil.drawText(text,0,0,backgroundValue.getValue(),bgColorValue.getValue(),textColorValue.getValue());
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.FONT_HEIGHT;
    }
}
