package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ModeValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.*;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;

public class ArmorDisplay extends Element {

    private final BooleanValue heldItem = new BooleanValue("显示手持物品","Show Held Item",true);
    private final BooleanValue mode = new BooleanValue("竖向显示","Horizontal",false);
    private final BooleanValue displayDamage = new BooleanValue("显示耐久","Display Damage",true);

    public ArmorDisplay() {
        super("ArmorDisplay","装备显示");
    }
    @Override
    public void onDraw() {

        int x;
        int y;
        int addHeldItem = heldItem.getValue() ? 16 : 0;

        ItemStack sword = new ItemStack(Items.diamond_sword);

        if(!mode.getValue()) {
        	x = 65 + addHeldItem;
        	y = 16;
        } else {
        	x = 16;
        	y = 65 + addHeldItem;
        }

        for (int i21 = 0; i21 < mc.thePlayer.inventory.armorInventory.length; ++i21) {
        	
            final ItemStack is = mc.thePlayer.inventory.armorInventory[i21];
            
            if(mc.currentScreen instanceof GuiChat) {
            	this.renderFakeArmorStatus();
            }else {
                this.renderArmorStatus(i21, is);
            }
        }

        if (heldItem.getValue()) {
            GL11.glPushMatrix();
            RenderHelper.enableGUIStandardItemLighting();

            try {
                int itemX = (!mode.getValue() ? (-16 * -1 + 48) : 0);
                int itemY = (!mode.getValue() ? 0 : (-16 * -1 + 48));

                ItemStack itemStack = mc.thePlayer.getHeldItem();
                if (itemStack != null) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, itemX, itemY);

                    if (displayDamage.getValue()) {
                        Item item = itemStack.getItem();
                        if (item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemBow) {
                            int durability = itemStack.getMaxDamage() - itemStack.getItemDamage();
                            int strX = mode.getValue() ? itemX + 16 : itemX;
                            int strY = mode.getValue() ? itemY : itemY + 16;
                            FontManager.client().drawStringWithShadow(String.valueOf(durability), strX, strY, -1);
                        }
                    }
                }
            } finally {
                // 确保状态总是被正确恢复
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableLighting();
                GL11.glPopMatrix();
            }
        }

        width = (x);
        height =(y);
        super.onDraw();
    }

	private void renderFakeArmorStatus() {

        boolean horizontal = !mode.getValue();

        ItemStack helmet = new ItemStack(Items.diamond_helmet);
        ItemStack chestPlate = new ItemStack(Items.diamond_chestplate);
        ItemStack leggings = new ItemStack(Items.diamond_leggings);
        ItemStack boots = new ItemStack(Items.diamond_boots);

        mc.getRenderItem().renderItemAndEffectIntoGUI(helmet, (horizontal ? -16 * 3 + 48 : 0), (horizontal ? 0 : -16 * 3 + 48));
        mc.getRenderItem().renderItemAndEffectIntoGUI(chestPlate, (horizontal ? -16 * 2 + 48 : 0), (horizontal ? 0 : -16 * 2 + 48));
        mc.getRenderItem().renderItemAndEffectIntoGUI(leggings, (horizontal ? -16 + 48 : 0), (horizontal ? 0 : -16 + 48));
        mc.getRenderItem().renderItemAndEffectIntoGUI(boots, (horizontal ? 48 : 0), (horizontal ? 0 : 48));
	}

    private void renderArmorStatus(final int pos, final ItemStack itemStack) {
        if (itemStack == null) return;

        int posXAdd;
        int posYAdd;

        if(!mode.getValue()) {
            posXAdd = -16 * pos + 48;
            posYAdd = 0;
        } else {
            posXAdd = 0;
            posYAdd = -16 * pos + 48;
        }

        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, posXAdd, posYAdd);
        if (!displayDamage.getValue()) return;
        int durability = itemStack.getMaxDamage() - itemStack.getItemDamage();

        int strX = posXAdd;
        int strY = posYAdd;
        if(mode.getValue()) strX += 16; else strY += 16;
        FontManager.client().drawStringWithShadow(String.valueOf(durability), strX, strY,-1);
    }
}