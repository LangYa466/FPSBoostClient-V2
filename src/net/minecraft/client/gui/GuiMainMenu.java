package net.minecraft.client.gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import net.fpsboost.Client;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.screen.alt.GuiAltManager;
import net.fpsboost.util.ColorUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.util.network.WebUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import javax.swing.*;

import static net.fpsboost.util.network.WebUtil.bindLocalTexture;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {

    public static final File file = new File(ConfigManager.dir,"bg.data");
    public static String localimg = "";

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui() {
        int buttonY = this.height / 4 + 48;
        if ((Config.getGameSettings().language.equals("en_US"))) {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, buttonY, I18n.format("menu.singleplayer")));
            this.buttonList.add(new GuiButton(2, this.width / 2 - 100, buttonY + 24, I18n.format("menu.multiplayer")));
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, buttonY + 48, I18n.format("menu.altmanager")));
            this.buttonList.add(new GuiButton(114514, this.width / 2 - 100, buttonY + 72, I18n.format("menu.setBG")));
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, buttonY + 96 + 12, 98, 20, I18n.format("menu.options")));
            this.buttonList.add(new GuiButton(4, this.width / 2 + 2, buttonY + 96 + 12, 98, 20, I18n.format("menu.quit")));
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

    protected ResourceLocation actionPerformed(GuiButton button) throws IOException {
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
        if (button.id == 5) {
            this.mc.displayGuiScreen(new GuiLanguage(this,mc.gameSettings,mc.getLanguageManager()));
        }
        if (button.id == 114514) {
            String[] options = {!ClientSettings.INSTANCE.cnMode.getValue()?"Online": "在线", !ClientSettings.INSTANCE.cnMode.getValue()?"Local":"本地", !ClientSettings.INSTANCE.cnMode.getValue()?"Cancel":"取消"};
            int result = JOptionPane.showOptionDialog(
                    null,                         // 父组件
                    !ClientSettings.INSTANCE.cnMode.getValue()?"Switch to Online mode?":"请选择一个选项",             // 消息
                    !ClientSettings.INSTANCE.cnMode.getValue()?"Online Mode":"选项对话框",                 // 对话框标题
                    JOptionPane.DEFAULT_OPTION,  // 默认选项
                    JOptionPane.INFORMATION_MESSAGE, // 消息类型
                    null,                         // 图标
                    options,                     // 选项按钮数组
                    options[0]                   // 默认选择的选项
            );

            if (result == JOptionPane.CLOSED_OPTION) {
                System.out.println(!ClientSettings.INSTANCE.cnMode.getValue()?"Closed the dialog.":"对话框被关闭");
            } else if (result == options.length - 1) {
                System.out.println(!ClientSettings.INSTANCE.cnMode.getValue()?"User cancelled.":"用户选择了取消");
            } else {
                System.out.println(!ClientSettings.INSTANCE.cnMode.getValue()?"User selected: " + options[result]:"用户选择了: " + options[result]);
            }
            if (result == 0){
            String url = JOptionPane.showInputDialog("输入壁纸获取的网页(带https://)(不能本地) 本地可以找图床上传");
            if (url == null) return null;
            WebUtil.bindTextureWithUrl(url,"ClientBG");
            if (!file.exists()) file.createNewFile();
            FileUtils.writeStringToFile(file,url);
            if (ClientSettings.INSTANCE.cnMode.getValue()) System.out.println("切换背景图成功"); else System.out.println("Background changed successfully");
            }
            if (result == 1){
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择本地图片文件");

                // 打开文件选择对话框
                int userSelection = fileChooser.showOpenDialog(null);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToLoad = fileChooser.getSelectedFile();
                    localimg = fileToLoad.getAbsolutePath();
                    if (!file.exists()) file.createNewFile();
                    FileUtils.writeStringToFile(file,fileToLoad.getAbsolutePath());
                    return bindLocalTexture(fileToLoad.getAbsolutePath(), "ClientBG");
                }
                if (ClientSettings.INSTANCE.cnMode.getValue()) System.out.println("切换背景图成功"); else System.out.println("Background changed successfully");
            }
        }
        return null;
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawClientBackground();
        if (Client.isOldVersion) FontManager.hanYi().drawStringWithShadow(!ClientSettings.INSTANCE.cnMode.getValue()?"Your version is not the latest! Please download the latest version from the group file!":"您的版本不是最新版! 请去群文件下载最新版!",5,5, ColorUtil.rainbow(3, (int) partialTicks).getRGB());
        FontManager.hanYi(70).drawCenteredStringWithShadow("FPSBoost",width / 2F,height / 4F,-1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
