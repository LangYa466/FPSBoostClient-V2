package net.fpsboost.screen;

import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * @author LangYa
 * @since 2024/12/25 23:00
 */
public class GuiI18n extends GuiScreen {

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(1,width / 2 - 40,height / 2 - 20,80,20,"简体中文"));
        buttonList.add(new GuiButton(2,width / 2 - 40,height / 2 + 5,80,20,"English"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontManager.client(70).drawCenteredStringWithShadow("选择你得语言",width / 2F,height / 4F,-1);
        drawClientBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            ClientSettings.INSTANCE.cnMode.setValue(true);
        } else {
            ClientSettings.INSTANCE.cnMode.setValue(false);
        }
        mc.displayGuiScreen(new GuiRectMode());
        return super.actionPerformed(button);
    }
}
