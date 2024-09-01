package net.fpsboost.element.impl;

import net.fpsboost.Client;
import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;

/**
 * @author LangYa
 * @since 2024/8/30 21:23
 */
public class DrawString extends Element {

    public DrawString() {
        super("dragString");
    }

    @Override
    public void onDraw() {
        RenderUtil.drawStringWithShadow(Client.name,0,0,-1);
    }

    @Override
    public void init() {
        width = mc.fontRendererObj.getStringWidth(Client.name);
        height = mc.fontRendererObj.FONT_HEIGHT;
    }
}
