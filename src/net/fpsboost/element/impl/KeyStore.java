package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.module.Module;
import net.fpsboost.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/8/30 21:49
 */
public class KeyStore extends Element {
    public KeyStore() {
        super("KeyStore");
    }

    private final Color bgColor = new Color(0, 0, 0, 102);
    private final Color pressbgColor = new Color(255, 255, 255, 102);

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
        RenderUtil.drawRect(x, y, width,height,key.isKeyDown() ? pressbgColor : bgColor);
        int textX = isSpace ? mc.fontRendererObj.getStringWidth(keyName) : mc.fontRendererObj.getStringWidth(keyName) * 2;
        RenderUtil.drawString(keyName, x + textX, y + mc.fontRendererObj.FONT_HEIGHT / 2 + 1, -1);
    }
}
