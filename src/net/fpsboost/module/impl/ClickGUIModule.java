package net.fpsboost.module.impl;

import net.fpsboost.screen.clickgui.ClickGui;
import net.fpsboost.module.Module;
import net.fpsboost.value.impl.ModeValue;
import org.lwjgl.input.Keyboard;

/**
 * @author LangYa
 * @since 2024/9/11 21:21
 */
public class ClickGUIModule extends Module {
    public ClickGUIModule() {
        super("ClickGUI", "点击页面", "The Screen ClickGUI","你看见的这个页面", Keyboard.KEY_RSHIFT);
    }

    public static ThemeType theme = ThemeType.Light;
    private static final ModeValue themeModeValue = new ModeValue("主题模式","Theme Mode","浅色","浅色","暗色") {
        @Override
        public void setValue(String value) {
            if (value.contains("浅色")) {
                theme = ThemeType.Light;
            } else {
                theme = ThemeType.Dark;
            }
            super.setValue(value);
        }
    };
    public enum ThemeType {
        Dark, Light
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(ClickGui.INSTANCE);
        toggle();
    }
}
