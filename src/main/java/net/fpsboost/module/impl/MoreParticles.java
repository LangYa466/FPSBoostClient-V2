package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumParticleTypes;

public class MoreParticles extends Module {
    private static final NumberValue crackSize = new NumberValue("粒子数量", "Number", 2, 10, 0, 1);
    private static final BooleanValue mode1 = new BooleanValue("攻击粒子", "Attack Particle", true);
    private static final BooleanValue mode2 = new BooleanValue("暴击粒子", "Critical Particle", true);

    public MoreParticles() {
        super("MoreParticles", "更多粒子");
    }

    //如果直接在mc里面的方法里面直接获取会浪费性能
    private static boolean isEnable;

    @Override
    public void onEnable() {
        isEnable = true;
    }

    @Override
    public void onDisable() {
        isEnable = false;
    }

    public static void onAttack(Entity entity) {
        if (!isEnable) return;
        if (entity.isDead || !(entity instanceof EntityLiving) || ((EntityLiving) entity).getHealth() == 0) return;
        for (int index = 0; index < crackSize.getValue().intValue(); ++index) {
            if (mode1.getValue()) mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
            if (mode2.getValue()) mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC);
        }
    }
}
