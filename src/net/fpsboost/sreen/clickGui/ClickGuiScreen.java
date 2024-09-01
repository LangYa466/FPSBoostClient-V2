package net.fpsboost.sreen.clickGui;

import net.minecraft.client.gui.GuiScreen;

/**
 * @author LangYa
 * @since 2024/9/1 20:04
 */
public class ClickGuiScreen extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
