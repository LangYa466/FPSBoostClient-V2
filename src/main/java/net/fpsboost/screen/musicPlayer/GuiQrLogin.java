package net.fpsboost.screen.musicPlayer;

import net.fpsboost.util.font.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

/**
 * @author LangYa466
 * @since 2/28/2025
 */
public class GuiQrLogin extends GuiScreen {
    private final GuiScreen parentScreen;
    private final String key;
    private final ResourceLocation qrImage;
    private long lastPollTime = 0;

    public GuiQrLogin(GuiScreen parentScreen, String key, ResourceLocation qrImage) {
        this.parentScreen = parentScreen;
        this.key = key;
        this.qrImage = qrImage;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        // 添加取消按钮
        buttonList.add(new GuiButton(0, width / 2 - 50, height - 40, 100, 20, "取消"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontManager.client().drawCenteredString("扫描二维码登录", width / 2F, height - 10, -1);
        // 绘制二维码图片 大小固定为 128x128 像素
        int imgWidth = 128;
        int imgHeight = 128;
        int x = (width - imgWidth) / 2;
        int y = (height - imgHeight) / 2;
        Minecraft.getMinecraft().getTextureManager().bindTexture(qrImage);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
        super.drawScreen(mouseX, mouseY, partialTicks);

        // 每隔 3 秒轮询二维码状态
        if (System.currentTimeMillis() - lastPollTime > 3000) {
            lastPollTime = System.currentTimeMillis();
            new Thread(() -> {
                int status = new NeteaseAPI().checkQrCodeStatus(key);
                if (status == 803) {
                    Minecraft.getMinecraft().displayGuiScreen(parentScreen);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("登录成功"));
                } else if (status == 800) {
                    Minecraft.getMinecraft().displayGuiScreen(parentScreen);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("二维码已过期 请重新获取"));
                }
                // 801：等待扫码；802：待确认 继续轮询
            }).start();
        }
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) {
        if (button.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(parentScreen);
        }
        return null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
