package net.fpsboost.element.impl.text;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;

import java.awt.*;

/**
 * @author LangYa466
 * @since 2/9/2025
 */
public class TextDisplay extends Element {
    public TextDisplay(String name,String cnName) {
        super(name, cnName);
        this.values.add(backgroundValue);
        this.values.add(textShadowValue);
        this.values.add(clientFontValue);
    }

    private final int cacheMCFontHeight = mc.fontRendererObj.getHeight();
    private final BooleanValue backgroundValue = new BooleanValue("背景","Background",true);
    private final BooleanValue textShadowValue = new BooleanValue("字体阴影","Text Shadow",true);
    private final BooleanValue clientFontValue = new BooleanValue("更好的字体","Better Font",true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色","Background Color",new Color(0,0,0,80),this);
    private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color",Color.white,this);

    public String getText() {
        return "Null";
    }

    @Override
    public void onDraw() {
        width = RenderUtil.drawText(getText(),0,0,backgroundValue.getValue(),bgColorValue.getValueC(),textColorValue.getValueC(),textShadowValue.getValue(), clientFontValue.getValue());
    }

    @Override
    public void init() {
        height = cacheMCFontHeight;
    }
}
