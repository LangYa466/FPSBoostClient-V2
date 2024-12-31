package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;

import net.fpsboost.module.impl.ClientSettings;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public class GuiDisconnected extends GuiScreen {
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp != null ? chatComp : new ChatComponentText("Disconnected from the server."); // Fallback message
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {}

    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.getHeight();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.getHeight(), I18n.format("gui.toMenu")));

        String buttonText = (ClientSettings.INSTANCE.cnMode.getValue()) ? "重连服务器" : "ReConnect";
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.getHeight() + 24, buttonText));
    }

    protected ResourceLocation actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else {
            if (this.mc.getCurrentServerData() == null) {
                this.mc.displayGuiScreen(new GuiMainMenu()); // Handle null server data case
            } else {
                this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, this.mc.getCurrentServerData()));
            }
        }
        return null;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.getHeight() * 2, 11184810);
        int i = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null) {
            for (String s : this.multilineMessage) {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += this.fontRendererObj.getHeight();
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
