package net.fpsboost.element.impl;

import net.fpsboost.element.Element;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ModeValue;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArmorDisplay extends Element {

    private final BooleanValue heldItem = new BooleanValue("显示手持物品",true);
    private final ModeValue mode = new ModeValue("显示方向","横","横","竖");

    public ArmorDisplay() {
        super("ArmorDisplay","装备显示");
    }
    @Override
    public void onDraw() {
        boolean horizontal = mode.isMode("横");

        int x = xPos;
        int y;
        int addHeldItem = heldItem.getValue() ? 16 : 0;

        ItemStack sword = new ItemStack(Items.diamond_sword);

        if(mode.isMode("横")) {
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
        
        if(heldItem.getValue()) {
            GL11.glPushMatrix();
            RenderHelper.enableGUIStandardItemLighting();

            if (mc.currentScreen instanceof GuiChat) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(sword, (int) (xPos + (horizontal ? (-16 * -1 + 48) : 0)), (int) (yPos + (horizontal ? 0 : (-16 * -1 + 48))));
            } else {
                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getHeldItem(), (int) (xPos + (horizontal ? (-16 * -1 + 48) : 0)), (int) (yPos + (horizontal ? 0 : (-16 * -1 + 48))));
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GL11.glPopMatrix();
        }

        width = (x);
        height =(y);
        super.onDraw();
    }
	
	private void renderFakeArmorStatus() {

        boolean horizontal;
        
        ItemStack helmet = new ItemStack(Items.diamond_helmet);
        ItemStack chestplate = new ItemStack(Items.diamond_chestplate);
        ItemStack leggings = new ItemStack(Items.diamond_leggings);
        ItemStack boots = new ItemStack(Items.diamond_boots);

        horizontal = mode.isMode("横");
        
        mc.getRenderItem().renderItemAndEffectIntoGUI(helmet, (int) (xPos + (horizontal ? -16 * 3 + 48 : 0)), (int) (yPos + (horizontal ? 0 : -16 * 3 + 48)));
        mc.getRenderItem().renderItemAndEffectIntoGUI(chestplate, (int) (xPos + (horizontal ? -16 * 2 + 48 : 0)), (int) (yPos + (horizontal ? 0 : -16 * 2 + 48)));
        mc.getRenderItem().renderItemAndEffectIntoGUI(leggings, (int) (xPos + (horizontal ? -16 * 1 + 48 : 0)), (int) (yPos + (horizontal ? 0 : -16 * 1 + 48)));
        mc.getRenderItem().renderItemAndEffectIntoGUI(boots, (int) (xPos + (horizontal ? -16 * 0 + 48 : 0)), (int) (yPos + (horizontal ? 0 : -16 * 0 + 48)));
	}

    private void renderArmorStatus(final int pos, final ItemStack itemStack) {


        if (itemStack == null) {
            return;
        }

        int posXAdd;
        int posYAdd;
        RenderItem itemRender = mc.getRenderItem();

        if(mode.isMode("横")) {
            posXAdd = -16 * pos + 48;
            posYAdd = 0;
        } else {
            posXAdd = 0;
            posYAdd = -16 * pos + 48;
        }

        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) (xPos + posXAdd), (int) (yPos + posYAdd));
    }
}