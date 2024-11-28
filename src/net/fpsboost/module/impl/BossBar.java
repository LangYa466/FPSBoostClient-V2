package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.boss.BossStatus;

public class BossBar extends Module {
    public static final BossBar INSTANCE = new BossBar();
    public BossBar() {
        super("BossBar","Boss血条","Edit the boss bar","修改boss血条位置");
    }
    ScaledResolution scaledresolution = new ScaledResolution(this.mc);
    public BooleanValue ishide = new BooleanValue("隐藏怪物血条", "Hide Boss Bar",  false);
    public int i = scaledresolution.getScaledWidth();
    public int h = scaledresolution.getScaledHeight();
    public int j = 182;
    public int k = i / 2 - j / 2;
    public int l = (int)(BossStatus.healthScale * (float)(j + 1));
    public int i1 = 12;
    public NumberValue x = new NumberValue("X坐标", "X Position", k, i, 0, 1);
    public NumberValue y = new NumberValue("Y坐标", "Y Position", k, h, 0, 1);
}
