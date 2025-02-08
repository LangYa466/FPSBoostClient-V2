package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.TextValue;

import java.awt.*;

/**
 * @author LangYa466
 * @since 2/3/2025
 */
public class SpeedDisplay extends Element {
    public SpeedDisplay() {
        super("SpeedDisplay", "速度显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景","Background",true);
    private final BooleanValue textShadowValue = new BooleanValue("字体阴影","Text Shadow",true);
    private final BooleanValue clientFontValue = new BooleanValue("更好的字体","Better Font",true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色","Background Color",new Color(0,0,0,80),this);
    private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color",Color.white,this);
    private final TextValue textFormatValue = new TextValue("文本格式","Text Format","%s m/s");

    @Override
    public void onDraw() {
        String text = String.format(textFormatValue.getValue(), getBPS());
        width = RenderUtil.drawText(text,0,0,backgroundValue.getValue(),bgColorValue.getValueC(),textColorValue.getValueC(),textShadowValue.getValue(), clientFontValue.getValue());
    }

    private double getBPS() {
        if (mc.thePlayer == null) return 0.0;

        double x = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double z = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        double speed = Math.sqrt(x * x + z * z) * 20;

        return Math.round(speed * 10.0) / 10.0; // 保留一位小数
    }


    @Override
    public void init() {
        height = mc.fontRendererObj.getHeight();
    }
}
