package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

import java.io.IOException;

/**
 * @author LangYa
 * @since 2024/10/30 21:46
 */
public class AutoLuGuan extends Module {
    public AutoLuGuan() {
        super("AutoLuGuan", "自动炉管");
    }

    @Override
    public void onEnable() {
        try {
            Runtime.getRuntime().exec("cmd /c start https://cn.pornhub.com/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
        enable = false;
        super.onEnable();
    }
}
