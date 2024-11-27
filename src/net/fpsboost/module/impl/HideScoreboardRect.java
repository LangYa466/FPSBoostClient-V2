package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;

/**
 * @author LangYa
 * @since 2024/10/30 21:32
 */
public class HideScoreboardRect extends Module {
    public static final BooleanValue rednumber = new BooleanValue("rednumber", "红字数字", false);
    public HideScoreboardRect() {
        super("HideScoreboardRect", "记分板背景隐藏","Disable the background of the scoreboard", "隐藏记分板的背景");
    }
}
