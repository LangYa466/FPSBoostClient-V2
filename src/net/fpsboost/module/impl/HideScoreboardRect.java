package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;

/**
 * @author LangYa
 * @since 2024/10/30 21:32
 */
public class HideScoreboardRect extends Module {
    public static final BooleanValue rednumber = new BooleanValue("红字数字", "RedNumber", false);
    public HideScoreboardRect() {
        super("HideScoreboardRect", "记分板背景隐藏","Disable the background of the scoreboard", "隐藏记分板的背景");
    }

    //如果直接在mc里面的方法里面直接获取会浪费性能
    public static boolean isEnable;

    @Override
    public void onEnable() {
        isEnable = true;
    }

    @Override
    public void onDisable() {
        isEnable = false;
    }
}
