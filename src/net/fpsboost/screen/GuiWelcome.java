package net.fpsboost.screen;

import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/**
 * @author LangYa
 * @since 2024/11/20 19:14
 */
public class GuiWelcome extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        String infoText = "客户端使用教程 按一下RSHIFT进行下一步";
        mc.fontRendererObj.drawStringWithShadow(infoText,width / 2 - mc.fontRendererObj.getStringWidth(infoText),height / 2,-1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }
}
