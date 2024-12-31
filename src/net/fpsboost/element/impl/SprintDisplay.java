package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.module.impl.Sprint;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;

import javax.swing.*;
import java.awt.*;

/**
 * @author LangYa
 * @since 2024/12/31 05:13
 */
public class SprintDisplay extends Element {

    public SprintDisplay() {
        super("SprintDisplay", "疾跑显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景","Background",true);
    private final BooleanValue textShadowValue = new BooleanValue("字体阴影","Text Shadow",true);
    private final BooleanValue clientFontValue = new BooleanValue("更好的字体","Better Font",true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色","Background Color",new Color(0,0,0,80));
    private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color",Color.white);

    @Override
    public void onDraw() {
        String text = "Sprint: " + (isSprinting() ? "Toggled" : "Vanilla");
        width = RenderUtil.drawText(text,0,0,backgroundValue.getValue(),bgColorValue.getValue(),textColorValue.getValue(),textShadowValue.getValue(), clientFontValue.getValue());
    }

    private boolean isSprinting() {
        return ModuleManager.isEnabled(Sprint.class);
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.getHeight();
    }
}
