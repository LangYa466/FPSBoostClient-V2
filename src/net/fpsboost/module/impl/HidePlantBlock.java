package net.fpsboost.module.impl;

import net.fpsboost.Wrapper;
import net.fpsboost.handler.MessageHandler;
import net.fpsboost.module.Module;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author LangYa466
 * @since 2/4/2025
 */
public class HidePlantBlock extends Module {
    public HidePlantBlock() {
        super("HidePlantBlock", "隐藏植物方块");
    }

    private final NumberValue radiusValue = new NumberValue("隐藏范围", "Radius", 16, 64, 2, 1);

    @Override
    public void onRender3D() {
        int radius = radiusValue.getValue().intValue();
        World world = mc.theWorld;
        if (Wrapper.isNull()) return;

        int px = (int) mc.thePlayer.posX;
        int py = (int) mc.thePlayer.posY;
        int pz = (int) mc.thePlayer.posZ;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos blockPos = new BlockPos(px + x, py + y, pz + z);
                    Block block = world.getBlockState(blockPos).getBlock();
                    if (block instanceof BlockBush) { // 只隐藏植物方块
                        world.setBlockToAir(blockPos);
                    }
                }
            }
        }
        super.onRender3D();
    }

    @Override
    public void onWorldLoad() {
        info();
        super.onWorldLoad();
    }

    public static Block getBlock(final BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    @Override
    public void onDisable() {
        info();
        super.onDisable();
    }

    private void info() {
        for (int i = 0; i < 5; i++) {
            MessageHandler.addMessage("关闭此功能后之前清除的不会回来", MessageHandler.MessageType.Warning);
        }
    }
}
