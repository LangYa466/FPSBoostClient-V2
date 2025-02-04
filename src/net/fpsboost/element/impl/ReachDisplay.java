package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/12/31 05:09
 */
public class ReachDisplay extends Element {

    public ReachDisplay() {
        super("ReachDisplay", "距离显示");
    }

    private final NumberValue customLengthValue = new NumberValue("保留小数点位数","Reserved digits",1,10,0,1);
    private final BooleanValue backgroundValue = new BooleanValue("背景","Background",true);
    private final BooleanValue textShadowValue = new BooleanValue("字体阴影","Text Shadow",true);
    private final BooleanValue clientFontValue = new BooleanValue("更好的字体","Better Font",true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色","Background Color",new Color(0,0,0,80),this);
    private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color",Color.white, this);

    private static double range;

    // 死吗render distance
    public static void onAttack(Entity targetEntity) {
        double distance = mc.thePlayer.getDistanceToEntity(targetEntity);

        // 修正 别问为什么要这样减少 不信自己获取getDistanceToEntity试试看
        double offset = distance > 2 ? 1.62 : 0.8;

        range = Math.max(0, distance - offset);
    }

    @Override
    public void onDraw() {
        int decimalPlaces = customLengthValue.getValue().intValue();
        String format = "%." + decimalPlaces + "f Blocks";
        String text = String.format(format, range);
        width = RenderUtil.drawText(text,0,0,backgroundValue.getValue(),bgColorValue.getValueC(),textColorValue.getValueC(), textShadowValue.getValue(), clientFontValue.getValue());
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.getHeight();
    }
}
