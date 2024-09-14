package net.fpsboost.handler;

import net.fpsboost.Wrapper;
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
    
    public static void onAttack(Entity targetEntity) {
        target = targetEntity;
        sentAttack = target.getEntityId();
        sentAttackTime = System.currentTimeMillis();
    }

    public static void onUpdate() {
        if (System.currentTimeMillis() - lastHitTime > 2000L) {
            currentCombo = 0;
        }
    }

    public static void onWorldLoad() {
        currentCombo = 0;
        target = null;

    }

    public static void onPacketReceived(Packet<?> packet) {
        if (target == null) return;
        if (packet instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus s19 = (S19PacketEntityStatus) packet;
            if (s19.getOpCode() == 2) {
                Entity target = ((S19PacketEntityStatus) packet).getEntity(mc.theWorld);
                if (target != null) {
                    if (sentAttack != -1 && target.getEntityId() == sentAttack) {
                        sentAttack = -1;
                        if (System.currentTimeMillis() - sentAttackTime > 2000L) {
                            sentAttackTime = 0L;
                            currentCombo = 0;
                            return;
                        }

                        if (lastAttackId == target.getEntityId()) {
                            ++currentCombo;
                        } else {
                            currentCombo = 1;
                        }

                        lastHitTime = System.currentTimeMillis();
                        lastAttackId = target.getEntityId();
                    } else if (target.getEntityId() == mc.thePlayer.getEntityId()) {
                        currentCombo = 0;
                    }
                }
            }
        }
    }
}
