package net.fpsboost.screen.alt;

import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public abstract class GuiAltLogin extends GuiScreen {
    private final GuiScreen previousScreen;
    private GuiTextField username;
    protected volatile String status = EnumChatFormatting.YELLOW + "等待切换中...";

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    protected ResourceLocation actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                if (!username.getText().isEmpty()) {
                    this.onLogin(username.getText(), "");
                } else {
                    status = EnumChatFormatting.RED + "登录失败!";
                }
                break;
            case 1:
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            case 1145:
                this.onLogin(StringUtils.randomString(StringUtils.ALPHA_POOL, 10), "");
        }
        return null;
    }

    public abstract void onLogin(String account, String password);

    public void drawScreen(int x, int y, float z) {
        this.drawDefaultBackground();
        drawBackground(0);
        this.username.drawTextBox();
        FontManager.client().drawCenteredStringWithShadow("离线登录", (float) (this.width / 2), 20.0F, -1);
        FontManager.client().drawCenteredStringWithShadow(status, (float) (this.width / 2), (this.height / 4 + 24) + 38, -1);
        if (this.username.getText().isEmpty() && !this.username.isFocused()) {
            FontManager.client().drawStringWithShadow("用户名", (float) (this.width / 2 - 96), (this.height / 4 + 24) + 72 - 4, -7829368);
        }

        super.drawScreen(x, y, z);
    }

    @Override
    public void initGui() {
        final int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, "登录"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 72 + 12 + 24, "返回"));
        this.username = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 100, var3 + 72 - 12, 200, 20);
        this.username.setFocused(true);
        this.username.setMaxStringLength(200);
    }

    @Override
    protected void keyTyped(char character, int key) throws IOException {
        super.keyTyped(character, key);

        if (character == '\t' && this.username.isFocused()) {
            this.username.setFocused(!this.username.isFocused());
        }

        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }

        this.username.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        this.username.mouseClicked(x, y, button);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
    }
}
