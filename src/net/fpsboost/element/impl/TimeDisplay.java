package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.NumberValue;

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
    private final NumberValue backgroundRadiusValue = new NumberValue("背景圆角值", 2D,10D,0D,1D);

    @Override
    public void onDraw() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);
        String text = String.format("Time: %s", formattedTime);
        width = RenderUtil.drawStringWithRounded(text,0,0,backgroundRadiusValue.value.intValue(),backgroundValue.value);
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.FONT_HEIGHT;
    }
}
