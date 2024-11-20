package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;

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

    private final BooleanValue backgroundValue = new BooleanValue("背景",true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色",new Color(0,0,0,80));
    private final ColorValue textColorValue = new ColorValue("文本颜色",Color.white);

    @Override
    public void onDraw() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);
        String text = String.format("Time: %s", formattedTime);
        width = RenderUtil.drawText(text,0,0,backgroundValue.getValue(),bgColorValue.getValue(),textColorValue.getValue());
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.FONT_HEIGHT;
    }
}
