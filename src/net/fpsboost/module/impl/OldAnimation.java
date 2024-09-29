package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;

public class OldAnimation extends Module {
    public static BooleanValue oldRod = new BooleanValue("鱼竿动画", true);
    public static BooleanValue oldBlock = new BooleanValue("放置动画", true);
    public static BooleanValue blockHit = new BooleanValue("防砍动画", true);
    public static BooleanValue oldBow = new BooleanValue("弓箭动画", true);
    public static BooleanValue oldSwing = new BooleanValue("挥手动画", true);

    public OldAnimation() {
        super("1.7Animation", "1.7动画", "旧版本动画");
    }

}