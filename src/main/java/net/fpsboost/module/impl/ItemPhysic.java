package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

public class ItemPhysic extends Module {
    public ItemPhysic() {
        super("ItemPhysic","物理掉落","Make your items drop in 2D","让你的物品达到2D掉落效果");
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
