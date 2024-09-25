package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;

public class OldAnimation extends Module {
    public final BooleanValue blockHit = new BooleanValue("防砍动画", true);
    public final BooleanValue oldEat = new BooleanValue("吃东西动画", true);
    public final BooleanValue oldBow = new BooleanValue("弓箭动画", true);

    public OldAnimation() {
        super("1.7Animation", "1.7动画", "旧版本动画");
    }

    @Override
    public void onUpdate() {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (this.enable && heldItem != null) {
            if (blockHit.getValue() && heldItem.getItemUseAction() == EnumAction.BLOCK) {
                attemptSwing();
            } else if (oldEat.getValue() && heldItem.getItemUseAction() == EnumAction.DRINK) {
                attemptSwing();
            } else if (oldBow.getValue() && heldItem.getItemUseAction() == EnumAction.BOW) {
                attemptSwing();
            }
        }
    }

    /**
     * Swings the player's arm if you're holding the attack and use item keys at the same time and looking at a block.
     */
    private void attemptSwing() {
        if (mc.thePlayer.getItemInUseCount() > 0) {
            final boolean mouseDown = mc.gameSettings.keyBindAttack.isKeyDown() &&
                    mc.gameSettings.keyBindUseItem.isKeyDown();
            if (mouseDown && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                forceSwingArm();
            }
        }
    }

    /**
     * Forces the player to swing their arm.
     */
    private void forceSwingArm() {
        EntityPlayerSP player = mc.thePlayer;
        int swingEnd = player.isPotionActive(Potion.digSpeed) ?
                (6 - (1 + player.getActivePotionEffect(Potion.digSpeed).getAmplifier())) : (player.isPotionActive(Potion.digSlowdown) ?
                (6 + (1 + player.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2) : 6);
        if (!player.isSwingInProgress || player.swingProgressInt >= swingEnd / 2 || player.swingProgressInt < 0) {
            player.swingProgressInt = -1;
            player.isSwingInProgress = true;
        }
    }
    
}