package net.minecraft.client.gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import net.fpsboost.Client;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.screen.alt.GuiAltManager;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.util.network.WebUtil;
import net.minecraft.client.resources.I18n;
import org.apache.commons.io.FileUtils;

import javax.swing.*;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {

    public static final File file = new File(ConfigManager.dir,"bg.data");

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui() {
        int buttonY = this.height / 4 + 48;
        if (!ClientSettings.INSTANCE.cnMode.getValue()) {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, buttonY, I18n.format("menu.singleplayer")));
            this.buttonList.add(new GuiButton(2, this.width / 2 - 100, buttonY + 24, I18n.format("menu.multiplayer")));
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, buttonY + 48, I18n.format("menu.altmanager")));
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, buttonY + 72, I18n.format("menu.setBG")));
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, buttonY + 96 + 12, 98, 20, I18n.format("menu.options")));
            this.buttonList.add(new GuiButton(4, this.width / 2 + 2, buttonY + 96 + 12, 98, 20, I18n.format("menu.quit")));
            this.buttonList.add(new GuiButton(114514, this.width / 2 + 2, buttonY + 96, 98, 20, I18n.format("menu.setBG")));
        } else {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, buttonY, "单人游戏"));
            this.buttonList.add(new GuiButton(2, this.width / 2 - 100, buttonY + 24, "多人游戏"));
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, buttonY + 48, "账号管理器"));
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, buttonY + 96 + 12, 98, 20, "设置"));
            this.buttonList.add(new GuiButton(114514, this.width / 2 - 100, buttonY + 72, "切换背景图"));
            this.buttonList.add(new GuiButton(4, this.width / 2 + 2, buttonY + 96 + 12, 98, 20, "退出"));
        }
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, buttonY + 96 + 12));
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
        if (button.id == 114514) {
            String url = JOptionPane.showInputDialog("输入壁纸获取的网页(带https://)(不能本地) 本地可以找图床上传");
            WebUtil.bindTextureWithUrl(url,"ClientBG");
            if (!file.exists()) file.createNewFile();
            FileUtils.writeStringToFile(file,url);
            if (ClientSettings.INSTANCE.cnMode.getValue()) System.out.println("切换背景图成功"); else System.out.println("Background changed successfully");
        }
    }

    public static Color rainbow(int speed, int index) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawClientBackground();
        if (Client.isOldVersion) FontManager.hanYi().drawStringWithShadow("您的版本不是最新版! 请去群文件下载最新版!",5,5,rainbow(3, (int) partialTicks).getRGB());
        FontManager.hanYi(70).drawCenteredStringWithShadow("FPSBoost",width / 2F,height / 4F,-1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
