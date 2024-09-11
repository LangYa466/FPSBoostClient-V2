package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.minecraft.client.Minecraft;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author LangYa
 * @since 2024/8/30 21:23
 */
public class TimeDisplay extends Element {

    @Override
    public void onDraw() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);
        String text = String.format("Time: %s", formattedTime);
        width = RenderUtil.drawStringWithRounded(text,0,0);
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.FONT_HEIGHT;
    }
}
