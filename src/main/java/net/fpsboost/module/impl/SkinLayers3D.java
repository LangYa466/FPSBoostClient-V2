package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.NumberValue;

/**
 * @author LangYa466
 * @since 2/8/2025
 */
public class SkinLayers3D extends Module {
    public SkinLayers3D() {
        super("SkinLayers3D", "3D皮肤显示");
    }

    public static final NumberValue baseVoxelSize = new NumberValue("体素大小", "Voxel Size", 1.15, 1.01, 1.4, 0.01);
    public static final NumberValue bodyVoxelSize = new NumberValue("躯干体素宽度", "Torso Voxel Width", 1.05, 1.01, 1.4, 0.01);
    public static final NumberValue headVoxelSize = new NumberValue("头部体素大小", "Head Voxel Size", 1.18, 1.01, 1.25, 0.01);
    public static final NumberValue renderDistance = new NumberValue("细节层级距离", "Level Of Detail Distance", 14, 5, 40, 1);
    public static final NumberValue renderdistancelod = new NumberValue("细节层级距离", "renderdistancelod", 4, 50, 1, 5);

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
