package net.fpsboost.util;

import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * @author LangYa466
 * @since 2025/1/1
 */
public class ClientInputGUI extends GuiScreen {
    public int x,y;
    public String text;
    public int width = 100;
    public int height = 20;
    private GuiTextField guiTextField;

    public ClientInputGUI(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, x, y + 25, width, height, "确认"));
        guiTextField = new GuiTextField(1, this.fontRendererObj, x, y, width, height);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        guiTextField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        guiTextField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        text = guiTextField.getText();
        super.onGuiClosed();
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) throws IOException {
        if (button.id != 0) return null;
        onGuiClosed();
        mc.displayGuiScreen(null);
        return super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
