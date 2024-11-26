package net.minecraft.client.gui;

import java.io.IOException;

import net.fpsboost.screen.alt.GuiAltManager;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.resources.I18n;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui() {
        int buttonY = this.height / 4 + 48;

        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, buttonY, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, buttonY + 24, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, buttonY + 48, "游戏内切换MC账号"));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, buttonY + 72 + 12, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, buttonY + 72 + 12, 98, 20, I18n.format("menu.quit")));
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, buttonY + 72 + 12));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 3) {
            this.mc.displayGuiScreen(new GuiAltManager(this));
        }
        if (button.id == 4) {
            this.mc.shutdown();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FontManager.hanYi(70).drawCenteredStringWithShadow("FPSBoost",width / 2F,height / 4F,-1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
