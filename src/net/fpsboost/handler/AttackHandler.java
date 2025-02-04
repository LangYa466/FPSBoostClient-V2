package net.fpsboost.handler;

import net.fpsboost.Wrapper;
import net.fpsboost.element.impl.ReachDisplay;
import net.fpsboost.module.impl.AttackEffects;
import net.fpsboost.module.impl.MoreParticles;
import net.fpsboost.module.impl.TargetCircle;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S19PacketEntityStatus;

/**
 * @author LangYa
 * @since 2024/9/9 18:51
 */
public class AttackHandler implements Wrapper {

    public static Entity target = null;
    private static long sentAttackTime;
    private static long lastHitTime;
    private static int lastAttackId;
    public static int currentCombo;
    private static int sentAttack = -1;

    private static final long COMBO_RESET_TIME = 2000L; // 2 seconds

    public static void onAttack(Entity targetEntity) {
        target = targetEntity;
        sentAttack = target.getEntityId();
        sentAttackTime = System.currentTimeMillis();
        MoreParticles.onAttack(targetEntity);
        TargetCircle.onAttack(targetEntity);
        ReachDisplay.onAttack(targetEntity);
        AttackEffects.onAttack(targetEntity);
    }

    public static void onUpdate() {
        if (System.currentTimeMillis() - lastHitTime > COMBO_RESET_TIME) {
            currentCombo = 0;
        }
    }

    public static void onWorldLoad() {
        currentCombo = 0;
        target = null;
    }

    public static void onPacketReceived(Packet<?> packet) {
        if (target == null || !(packet instanceof S19PacketEntityStatus)) return;

        S19PacketEntityStatus s19 = (S19PacketEntityStatus) packet;
        if (s19.getOpCode() != 2) return;

        Entity entity = s19.getEntity(mc.theWorld);
        if (entity == null) return;

        if (entity.getEntityId() == sentAttack) {
            handleAttackCombo(entity);
        } else if (entity.getEntityId() == mc.thePlayer.getEntityId()) {
            currentCombo = 0;
        }
    }

    private static void handleAttackCombo(Entity target) {
        if (System.currentTimeMillis() - sentAttackTime > COMBO_RESET_TIME) {
            resetCombo();
            return;
        }

        if (lastAttackId == target.getEntityId()) {
            currentCombo++;
        } else {
            currentCombo = 1;
        }

        lastHitTime = System.currentTimeMillis();
        lastAttackId = target.getEntityId();
        sentAttack = -1;
    }

    private static void resetCombo() {
        sentAttackTime = 0L;
        currentCombo = 0;
    }
}
