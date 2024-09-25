package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import static net.fpsboost.util.ThemeUtil.*;

/**
 * @author LangYa
 * @since 2024/8/30 21:49
 */
public class KeyStore extends Element {

    public KeyStore() {
        super("KeyStore", "按键显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景",true);

    @Override
    public void onDraw() {
        drawKey(mc.gameSettings.keyBindForward,21, 0,false);
        drawKey(mc.gameSettings.keyBindLeft,0, 21,false);
        drawKey(mc.gameSettings.keyBindBack,21, 21,false);
        drawKey(mc.gameSettings.keyBindRight,42, 21,false);
        drawKey(mc.gameSettings.keyBindJump,0, 42,62,20,true);
    }

    @Override
    public void init() {
        width = 63;
        height = 63;
    }

    public void drawKey(KeyBinding key, int x, int y, boolean isSpace) {
        drawKey(key,x,y,20,20,isSpace);
    }

    public void drawKey(KeyBinding key, int x, int y, int width, int height, boolean isSpace) {
        String keyName = Keyboard.getKeyName(key.getKeyCode());
        if(backgroundValue.getValue()) RenderUtil.drawRect(x, y, width,height,key.isKeyDown() ? pressbgColor : bgColor);
        //         RenderUtil.drawRect(x, y, width,height,key.isKeyDown() ? pressbgColor : bgColor);
        int add = isSpace ? 1 : 2;
        int textX = mc.fontRendererObj.getStringWidth(keyName) * add;
        RenderUtil.drawString(keyName, x + textX, y + mc.fontRendererObj.FONT_HEIGHT / 2 + 1, key.isKeyDown() ? bgColor : pressbgColor);
    }
}
