package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.SoundUtil;
import net.fpsboost.value.impl.ModeValue;
import net.fpsboost.value.impl.NumberValue;
import org.apache.commons.lang3.RandomUtils;

public final class ClickSounds extends Module {
    public ClickSounds() {
        super("模拟点击音效","模拟点击音效");
        this.cnDescription ="让你每次按下鼠标都能模拟特殊鼠标声音";
    }

    private static final ModeValue sound = new ModeValue("模式", "Standard","Standard","Double","Alan");
    private static final NumberValue volume = new NumberValue("音量","Volume", 0.5, 2, 0.1, 0.1);
    private static final NumberValue variation = new NumberValue("时长","Variation", 5, 100, 0, 1);

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

    public static void onClick() {
        if (!isEnable) return;
        String soundName = "rise.click.standard";

        switch (sound.getValue()) {
            case "Double": {
                soundName = "rise.click.double";
                break;
            }

            case "Alan": {
                soundName = "rise.click.alan";
                break;
            }
        }

        SoundUtil.playSound(soundName, volume.getValue().floatValue() * 2, RandomUtils.nextFloat(1.0F, 1 + variation.getValue().floatValue() / 100f));
    }
}
