package net.fpsboost.screen;

import lombok.Getter;
import net.fpsboost.util.Logger;
import net.fpsboost.util.network.WebUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * @author LangYa466
 * @since 2025/1/1
 */
public class GuiBackgroundSettings extends GuiScreen {
    private GuiTextField inputUrlField;
    private GuiButton buttonOnline;
    @Getter
    private GuiButton buttonLocal;
    @Getter
    private GuiButton buttonCancel;
    private final boolean cnMode;

    public GuiBackgroundSettings(boolean cnMode) {
        this.cnMode = cnMode;
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2 + 20;

        // Add buttons for Online, Local and Cancel
        this.buttonList.clear();
        this.buttonList.add(this.buttonOnline = new GuiButton(0, centerX - 100, centerY - 25, 200, 20, cnMode ? "网络URL" : "NetWorkURL"));
        this.buttonList.add(this.buttonLocal = new GuiButton(1, centerX - 100, centerY, 200, 20, cnMode ? "本地" : "Local"));
        this.buttonList.add(this.buttonCancel = new GuiButton(2, centerX - 100, centerY + 25, 200, 20, cnMode ? "取消" : "Cancel"));

        // Add URL input field for Online mode
        this.inputUrlField = new GuiTextField(3, this.fontRendererObj, centerX - 100, centerY - 80, 200, 20);
        this.inputUrlField.setMaxStringLength(100);
        this.inputUrlField.setText("输入URL在这里输");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawClientBackground();

        // Draw title and instructions
        String title = cnMode ? "选项对话框" : "Online Mode";
        String instruction = cnMode ? "请选择一个选项" : "Switch to Online mode?";

        drawCenteredString(this.fontRendererObj, title, this.width / 2, this.height / 4, -1);
        drawCenteredString(this.fontRendererObj, instruction, this.width / 2, this.height / 4 + 25, -1);

        if (buttonOnline.visible) {
            this.inputUrlField.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            String url = inputUrlField.getText();
            if (url.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入一个有效的URL！");
                return null;
            }
            try {
                WebUtil.bindTextureWithUrl(url, "ClientBG");
                File file = new File(mc.mcDataDir, "clientbg.txt");
                if (!file.exists()) file.createNewFile();
                FileUtils.writeStringToFile(file, url);
                JOptionPane.showMessageDialog(null, cnMode ? "切换背景图成功" : "Background changed successfully");
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        } else if (button.id == 1) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(cnMode ? "选择本地图片文件" : "Choose Local Image File");

            int userSelection = fileChooser.showOpenDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = fileChooser.getSelectedFile();
                String localImgPath = fileToLoad.getAbsolutePath();
                File file = new File(mc.mcDataDir, "clientbg.txt");
                if (!file.exists()) file.createNewFile();
                try {
                    FileUtils.writeStringToFile(file, localImgPath);
                    WebUtil.bindLocalTexture(localImgPath, "ClientBG");
                    JOptionPane.showMessageDialog(null, cnMode ? "切换背景图成功" : "Background changed successfully");
                } catch (IOException e) {
                    Logger.error(e.getMessage());
                }
            }
        } else if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
        return null;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.inputUrlField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

}
