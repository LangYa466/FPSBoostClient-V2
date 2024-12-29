package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import java.awt.*;

import static net.fpsboost.util.ThemeUtil.*;

/**
 * @author LangYa
 * @since 2024/8/30 21:49
 */
public class KeyStore extends Element {

    public KeyStore() {
        super("KeyStore", "按键显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景","Background", true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色","Background Color", new Color(0, 0, 0, 80));
    private final ColorValue pressBgColorValue = new ColorValue("按下时背景颜色","Pressed Background Color", new Color(255, 255, 255, 80));
    private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color", Color.white);
    private final ColorValue pressTextColorValue = new ColorValue("按下时文本颜色","Pressed Text Color", Color.black);
    private final BooleanValue clientFontValue = new BooleanValue("更好的字体","Better Font",true);

    @Override
    public void onDraw() {
        drawKey(mc.gameSettings.keyBindForward, 21, 0, false);
        drawKey(mc.gameSettings.keyBindLeft, 0, 21, false);
        drawKey(mc.gameSettings.keyBindBack, 21, 21, false);
        drawKey(mc.gameSettings.keyBindRight, 42, 21, false);
        drawKey(mc.gameSettings.keyBindJump, 0, 42, 62, 20, true);
    }

    @Override
    public void init() {
        width = 63;
        height = 63;
    }

    public void drawKey(KeyBinding key, int x, int y, boolean isSpace) {
        drawKey(key, x, y, 20, 20, isSpace);
    }

    public void drawKey(KeyBinding key, int x, int y, int width, int height, boolean isSpace) {
        String keyName = Keyboard.getKeyName(key.getKeyCode());
        if (backgroundValue.getValue())
            RenderUtil.drawRect(x, y, width, height, key.isKeyDown() ? pressBgColorValue.getValue() : bgColorValue.getValue());
        //         RenderUtil.drawRect(x, y, width,height,key.isKeyDown() ? pressbgColor : bgColor);
        int add = isSpace ? 1 : 2;
        int textX = mc.fontRendererObj.getStringWidth(keyName) * add;
        if (!clientFontValue.getValue()) {
            if (GameSettings.forceUnicodeFont)
                RenderUtil.drawString(keyName, x + textX, y + mc.fontRendererObj.getHeight() / 2 + 1, key.isKeyDown() ? pressTextColorValue.getValue() : textColorValue.getValue());
            else
                RenderUtil.drawCenterString(keyName, x + textX - 1, y + mc.fontRendererObj.getHeight() / 2 + 1, key.isKeyDown() ? pressTextColorValue.getValue() : textColorValue.getValue());
        } else {
            int textX1 = FontManager.hanYi().getStringWidth(keyName);
            if (isSpace) FontManager.hanYi().drawCenteredString(keyName, x + textX1, y + FontManager.hanYi().getHeight(), key.isKeyDown() ? pressTextColorValue.getValue() : textColorValue.getValue());
            else FontManager.hanYi().drawString(keyName,(key != mc.gameSettings.keyBindForward)  ? (x + textX1) : x + (textX1 / 2 + 1), y + (FontManager.hanYi().getHeight() / 2), key.isKeyDown() ? pressTextColorValue.getValue() : textColorValue.getValue());
        }
    }
}
