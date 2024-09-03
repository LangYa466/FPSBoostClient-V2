package net.fpsboost.sreen.clickGui;

import net.fpsboost.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

/**
 * @author LangYa
 * @since 2024/9/1 20:04
 */
public class ClickGuiScreen extends GuiScreen {

    private final Color bgColor = new Color(0,0,0,80);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(0);

        RenderUtil.drawRoundedRect(width / 2,height / 2,width / 4,height / 4,5,bgColor);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
