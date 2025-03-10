package net.fpsboost.screen.alt;

import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.util.Logger;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.List;

/**
 * @author LangYa466
 * @since 3/11/2025
 */
public class GuiLoggedAccounts extends GuiScreen {
    private final GuiScreen parentScreen;

    public GuiLoggedAccounts(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        buttonList.add(new GuiButton(0, this.width / 2 - 35, this.height - 40, 70, 20, ClientSettings.isChinese ? "返回" : "Back"));

        List<Session> alts = AltManager.Instance.getAltList();
        if (alts != null && !alts.isEmpty()) {
            int startY = 40;
            int buttonHeight = 20;
            int centerX = this.width / 2;

            for (int i = 0; i < alts.size(); i++) {
                int buttonY = startY + i * buttonHeight;
                buttonList.add(new GuiButton(i + 1, centerX - 80, buttonY, 60, buttonHeight, ClientSettings.isChinese ? "选择" : "Select"));
                buttonList.add(new GuiButton(i + 100, centerX + 20, buttonY, 60, buttonHeight, "Delete"));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawClientBackground();
        String title = ClientSettings.isChinese ? "登录过的账户" : "Logged In Accounts";
        FontManager.client().drawCenteredStringWithShadow(EnumChatFormatting.YELLOW + title, width / 2.0f, 20, -1);

        List<Session> alts = AltManager.Instance.getAltList();
        int startY = 40;
        int lineHeight = 20;
        int centerX = this.width / 2;

        if (alts != null && !alts.isEmpty()) {
            for (int i = 0; i < alts.size(); i++) {
                Session alt = alts.get(i);
                String display = alt.getUsername();
                FontManager.client().drawCenteredStringWithShadow(display, centerX, startY + i * lineHeight + (lineHeight - 8F) / 2, -1);
            }
        } else {
            String noAccounts = ClientSettings.isChinese ? "没有账户记录" : "No account records";
            FontManager.client().drawCenteredStringWithShadow(noAccounts, width / 2.0f, height / 2.0f, -1);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(parentScreen);
        } else {
            List<Session> alts = AltManager.Instance.getAltList();
            int index = button.id - 1;
            int deleteIndex = button.id - 100;

            if (index >= 0 && index < alts.size()) {
                Session selected = alts.get(index);
                mc.session = new Session(selected.getUsername(), selected.getPlayerID(), selected.getToken(), selected.getSessionType().sessionType);
            }

            if (deleteIndex >= 0 && deleteIndex < alts.size()) {
                alts.remove(deleteIndex);
            }

            initGui();
        }
        return null;
    }
}