package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.animation.Animation;
import net.fpsboost.util.animation.Direction;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemAppleGold;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/8/30 21:49
 */
public class KeyStore extends Element {

    public KeyStore() {
        super("KeyStore", "按键显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景","Background", true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色","Background Color", new Color(0, 0, 0, 80),this);
    private final ColorValue pressBgColorValue = new ColorValue("按下时背景颜色","Pressed Background Color", new Color(255, 255, 255, 80), this);
    private final BooleanValue pressBgAnimationValue = new BooleanValue("按下时背景动画","Pressed Background Animation",true);
    private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color", Color.white, this);
    private final ColorValue pressTextColorValue = new ColorValue("按下时文本颜色","Pressed Text Color", Color.black, this);
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
        boolean pressAnimation = pressBgAnimationValue.getValue();
        boolean background = backgroundValue.getValue();
        Animation clickAnimation = key.clickAnimation;

        // Draw background with animation if enabled
        if (background && pressAnimation) {
            if (!key.isKeyDown()) {
                RenderUtil.drawRect(x, y, width, height, bgColorValue.getValueC());
            }
            clickAnimation.setDirection(key.isKeyDown() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (!clickAnimation.finished(Direction.BACKWARDS)) {
                RenderUtil.scaleStart(x + width / 2f, y + height / 2f, clickAnimation.getOutput().floatValue());
                RenderUtil.drawRect(x, y, width, height, key.isKeyDown() ? pressBgColorValue.getValueC() : bgColorValue.getValueC());
                RenderUtil.scaleEnd();
            }
        } else if (background) {
            RenderUtil.drawRect(x, y, width, height, key.isKeyDown() ? pressBgColorValue.getValueC() : bgColorValue.getValueC());
        }

        // Calculate the correct X position for centered text
        int textWidth = mc.fontRendererObj.getStringWidth(keyName);
        int textX = x + (width - textWidth) / 2; // Center the text

        // Render text
        if (!clientFontValue.getValue()) {
            if (isSpace) {
                RenderUtil.drawString(keyName, textX, y + mc.fontRendererObj.getHeight() / 2 + 2, key.isKeyDown() ? pressTextColorValue.getValueC() : textColorValue.getValueC());
            } else {
                RenderUtil.drawCenterString(keyName, textX + 2, y + mc.fontRendererObj.getHeight() / 2 + 1, key.isKeyDown() ? pressTextColorValue.getValueC() : textColorValue.getValueC());
            }
        } else {
            if (isSpace) {
                int textX1 = FontManager.client().getStringWidth(keyName);
                FontManager.client().drawString(keyName, x + (width - textX1) / 2, y + (height / 2 - (FontManager.client().getHeight() / 1.5F)), key.isKeyDown() ? pressTextColorValue.getValueC() : textColorValue.getValueC());
            } else {
                FontManager.client().drawCenteredString(keyName, x + (width / 2) - 0.8F, y + FontManager.client().getHeight() + 2, key.isKeyDown() ? pressTextColorValue.getValueC() : textColorValue.getValueC());
            }
        }
    }
}
