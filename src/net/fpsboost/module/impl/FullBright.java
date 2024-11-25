package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

public class FullBright extends Module {
    private float old;

    public FullBright() {
        super("FullBright", "夜视");
    }

    @Override
    public void onEnable() {
        old = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onUpdate() {
        mc.gameSettings.gammaSetting = 1.5999999E7f;
        super.onUpdate();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = old;
    }
}
