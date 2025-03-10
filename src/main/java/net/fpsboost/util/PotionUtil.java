package net.fpsboost.util;

import net.fpsboost.Wrapper;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

/**
 * @author LangYa466
 * @since 3/11/2025
 */
public class PotionUtil implements Wrapper {

    public static int getPotionsFromInv(Potion inputPotion) {
        int count = 0;

        // 遍历 9-44 号槽位（避免索引错误）
        for (int i = 9; i < 45; i++) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (is == null || !(is.getItem() instanceof ItemPotion)) continue;

            ItemPotion potion = (ItemPotion) is.getItem();
            List<PotionEffect> effects = potion.getEffects(is);

            if (effects == null || effects.isEmpty()) continue;

            for (PotionEffect effect : effects) {
                if (effect.getPotionID() == inputPotion.id) {
                    count++;
                    break; // 一瓶药水只算一次，减少循环次数
                }
            }
        }

        return count;
    }

}
