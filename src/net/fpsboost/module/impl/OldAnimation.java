package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;

public class OldAnimation extends Module {
    public static BooleanValue oldRod = new BooleanValue("鱼竿动画","OldRod Animation", true);
    public static BooleanValue oldBlock = new BooleanValue("放置动画","Place Animation", true);
    public static BooleanValue blockHit = new BooleanValue("防砍动画","BlockHit Animation", true);
    public static BooleanValue oldBow = new BooleanValue("弓箭动画","Bow Animation", true);
    public static BooleanValue oldSwing = new BooleanValue("挥手动画","Swing Animation", true);

    public OldAnimation() {
        super("1.7Animation", "1.7动画","Animation for 1.7 version", "旧版本动画");
    }

    //如果直接在mc里面的方法里面直接获取会浪费性能
    public static boolean isEnabled;

    @Override
    public void onEnable() {
        isEnabled = true;
    }

    @Override
    public void onDisable() {
        isEnabled = false;
    }
}