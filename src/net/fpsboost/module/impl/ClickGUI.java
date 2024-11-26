package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.screen.SimpleClickGUI;
import org.lwjgl.input.Keyboard;

/**
 * @author LangYa
 * @since 2024/9/11 21:21
 */
public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", "点击页面", "The Screen ClickGUI","你看见的这个页面", Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new SimpleClickGUI());
        toggle();
    }
}
