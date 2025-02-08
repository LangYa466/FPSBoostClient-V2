package net.fpsboost.module.impl;

import net.fpsboost.Wrapper;
import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ModeValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.util.EnumParticleTypes;

/**
 * @author LangYa466
 * @since 2025/1/03
 */
public class AttackEffects extends Module {
    public static final ModeValue mode = new ModeValue("粒子模式","Mode", "血液", "雷电", "火焰", "爱心", "水滴", "血液");
    private static final NumberValue amount = new NumberValue("粒子数量","Number", 2, 10, 0, 1);
    
    private static final BooleanValue deadLighting = new BooleanValue("死亡雷电","DeadLighting", true);
    private static final BooleanValue lightingSoundValue = new BooleanValue("死亡的雷电的音效","LightingSound", true);
    private static EntityLivingBase target;

    public AttackEffects() {
        super("AttackEffects", "攻击特效");
    }

    public static boolean isEnable;

    @Override
    public void onEnable() {
        isEnable = true;
    }

    @Override
    public void onDisable() {
        isEnable = false;
    }

    public static void onAttack(Entity target) {
        if (!isEnable) return;
        if (!(target instanceof EntityLivingBase)) return;
        AttackEffects.target = (EntityLivingBase) target;
        if (((EntityLivingBase) target).getHealth() > 0 || !deadLighting.getValue()) return;
        mc.getNetHandler().handleSpawnGlobalEntity(new S2CPacketSpawnGlobalEntity(new EntityLightningBolt(mc.theWorld, target.posX, target.posY, target.posZ)));
        if (lightingSoundValue.getValue()) mc.thePlayer.playSound("entity.lightning.impact", 0.5f, 1f);
    }
    
    @Override
    public void onUpdate() {
        if (Wrapper.isNull()) return;
        if (target != null && target.hurtTime >= 3 && mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) < 10) {
            if (mc.thePlayer.ticksExisted > 3) {
                switch (mode.getValue().toLowerCase()) {
                    case "血液":
                        for (int i = 0; i < amount.getValue(); i++) {
                            mc.theWorld.spawnParticle(
                                    EnumParticleTypes.BLOCK_CRACK,
                                    target.posX, target.posY + target.height - 0.75, target.posZ,
                                    0.0, 0.0, 0.0,
                                    Block.getStateId(Blocks.redstone_block.getDefaultState())
                            );
                        }
                        break;
                    case "雷电":
                        mc.getNetHandler().handleSpawnGlobalEntity(
                                new S2CPacketSpawnGlobalEntity(
                                        new EntityLightningBolt(
                                                mc.theWorld, target.posX, target.posY, target.posZ
                                        )
                                )
                        );
                        if (lightingSoundValue.getValue()) {
                            mc.thePlayer.playSound("entity.lightning.impact", 0.5f, 1f);
                        }
                        break;
                    case "烟雾":
                        mc.effectRenderer.spawnEffectParticle(
                                EnumParticleTypes.SMOKE_NORMAL.getParticleID(),
                                target.posX, target.posY, target.posZ,
                                target.posX, target.posY, target.posZ
                        );
                        break;
                    case "水滴":
                        mc.effectRenderer.spawnEffectParticle(
                                EnumParticleTypes.WATER_DROP.getParticleID(),
                                target.posX, target.posY, target.posZ,
                                target.posX, target.posY, target.posZ
                        );
                        break;
                    case "爱心":
                        mc.effectRenderer.spawnEffectParticle(
                                EnumParticleTypes.HEART.getParticleID(),
                                target.posX, target.posY, target.posZ,
                                target.posX, target.posY, target.posZ
                        );
                        break;
                    case "火焰":
                        mc.effectRenderer.spawnEffectParticle(
                                EnumParticleTypes.LAVA.getParticleID(),
                                target.posX, target.posY, target.posZ,
                                target.posX, target.posY, target.posZ
                        );
                        break;
                }
            }
            target = null;
        }
    }
}
