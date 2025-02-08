package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.TextValue;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author LangYa
 * @since 2024/8/30 21:23
 */
public class TimeDisplay extends Element {
    public TimeDisplay() {
        super("TimeDisplay", "时间显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景","Background",true);
    private final BooleanValue textShadowValue = new BooleanValue("字体阴影","Text Shadow",true);
    private final BooleanValue clientFontValue = new BooleanValue("更好的字体","Better Font",true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色","Background Color",new Color(0,0,0,80),this);
    private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color",Color.white,this);
    private final TextValue textFormatValue = new TextValue("文本格式","Text Format","Time: %s");

    @Override
    public void onDraw() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);
        String text = String.format(textFormatValue.getValue(), formattedTime);
        width = RenderUtil.drawText(text,0,0,backgroundValue.getValue(),bgColorValue.getValueC(),textColorValue.getValueC(), textShadowValue.getValue(), clientFontValue.getValue());
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.getHeight();
    }
}
