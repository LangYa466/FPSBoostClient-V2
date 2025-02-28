package net.fpsboost.screen.musicPlayer;

import net.fpsboost.util.Logger;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author LangYa466
 * @since 2/28/2025
 */
public class GuiNeteaseMusic extends GuiScreen {
    private GuiTextField searchField;
    private List<Song> songs = new ArrayList<>();
    private int selectedSongIndex = -1;
    private NeteaseAPI api = new NeteaseAPI();
    private MusicPlayer player = new MusicPlayer();

    // 新增滚动控制变量：scrollOffset 表示当前列表起始索引，visibleRows 表示可见行数
    private int scrollOffset = 0;
    private final int visibleRows = 10;  // 一次最多显示10首歌

    @Override
    public void initGui() {
        int centerX = width / 2;
        int centerY = height / 2;
        // 创建搜索文本框
        searchField = new GuiTextField(0, FontManager.client(), centerX - 100, centerY - 80, 200, 20);
        buttonList.clear();
        // 添加搜索和扫码登录按钮
        buttonList.add(new GuiButton(1, centerX - 100, centerY - 50, 200, 20, "搜索"));
        buttonList.add(new GuiButton(2, centerX - 100, centerY - 20, 200, 20, "扫码登录"));
        // 添加滚动按钮(上、下)
        // 这里将滚动按钮放在歌曲列表右侧
        buttonList.add(new GuiButton(3, centerX + 110, centerY + 10, 20, 20, "↑"));
        buttonList.add(new GuiButton(4, centerX + 110, centerY + 10 + (visibleRows - 1) * 12, 20, 20, "↓"));
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        searchField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);

        int startY = height / 2 + 10;
        int endIndex = Math.min(scrollOffset + visibleRows, songs.size());

        for (int i = scrollOffset; i < endIndex; i++) {
            Song song = songs.get(i);
            int y = startY + (i - scrollOffset) * 12;
            if (i == selectedSongIndex) {
                drawRect(width / 2 - 100, y, width / 2 + 100, y + 12, 0x770000FF);
            }
            FontManager.client().drawString(song.getName() + " - " + song.getArtist(), width / 2 - 95, y + 2, -1);
        }

        // 绘制封面图
        if (selectedSongIndex != -1) {
            Song song = songs.get(selectedSongIndex);
            ResourceLocation cover = api.getSongCover(song.getId());
            if (cover != null) {
                RenderUtil.drawImage(cover, width / 2F - 50, height / 2F - 150, 100, 100);
            }
        }
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) {
        if (button.id == 1) { // 搜索按钮
            final String keyword = searchField.getText();
            new Thread(() -> {
                List<Song> result = api.search(keyword);
                songs.clear();
                songs.addAll(result);
                // 每次搜索后重置滚动和选中状态
                scrollOffset = 0;
                selectedSongIndex = -1;
            }).start();
        } else if (button.id == 2) { // 扫码登录按钮
            loginWithQr();
        } else if (button.id == 3) { // 向上滚动
            scrollUp();
        } else if (button.id == 4) { // 向下滚动
            scrollDown();
        }
        return null;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        // 判断是否点击到歌曲列表中的某一行
        int startY = height / 2 + 10;
        int endIndex = Math.min(scrollOffset + visibleRows, songs.size());
        for (int i = scrollOffset; i < endIndex; i++) {
            int y = startY + (i - scrollOffset) * 12;
            if (mouseX >= width / 2 - 100 && mouseX <= width / 2 + 100 &&
                    mouseY >= y && mouseY <= y + 12) {
                selectedSongIndex = i;
                Song song = songs.get(i);
                new Thread(() -> {
                    // 获取歌曲播放链接并播放
                    String url = api.getSongUrl(song.getId());
                    if (url != null) {
                        player.play(url);
                        // 获取歌词数据
                        List<LyricLine> lyrics = api.getLyric(song.getId());
                        if (!lyrics.isEmpty()) {
                            LyricDisplay.setLyrics(lyrics);
                            /*
                            // 如果需要切换到歌词显示界面，可以在主线程调用：
                            Minecraft.getMinecraft().addScheduledTask(() -> {
                                Minecraft.getMinecraft().displayGuiScreen(new GuiLyricScreen(lyrics));
                            });
                             */
                        } else {
                            Minecraft.getMinecraft().thePlayer.addChatMessage(
                                    new ChatComponentText("无法获取歌词"));
                        }
                    } else {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(
                                new ChatComponentText("无法获取歌曲链接"));
                    }
                }).start();
                break;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        searchField.textboxKeyTyped(typedChar, keyCode);
    }

    // 重写鼠标滚轮事件，支持鼠标滚动
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = org.lwjgl.input.Mouse.getEventDWheel();
        if (dWheel != 0) {
            if (dWheel > 0) {
                scrollUp();
            } else {
                scrollDown();
            }
        }
    }

    // 向上滚动：scrollOffset 不小于0
    private void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset--;
        }
    }

    // 向下滚动：scrollOffset 最大值为 songs.size() - visibleRows
    private void scrollDown() {
        if (scrollOffset < songs.size() - visibleRows) {
            scrollOffset++;
        }
    }

    private void loginWithQr() {
        try {
            // 调用接口获取二维码 key
            String key = api.getQrKey();
            if (key == null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("生成二维码 key 失败"));
                return;
            }
            // 调用接口获取二维码图片 Base64 编码
            String qrBase64 = api.getQrCodeImage(key);
            if (qrBase64 == null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("生成二维码失败"));
                return;
            }
            if (qrBase64.startsWith("data:image/png;base64,")) {
                qrBase64 = qrBase64.substring(22);
            }
            byte[] imageBytes = Base64.getDecoder().decode(qrBase64);
            // 将 Base64 转换为 BufferedImage 再生成动态纹理
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            DynamicTexture dynTex = new DynamicTexture(bufferedImage);
            ResourceLocation resLoc = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("qr", dynTex);
            // 切换到二维码登录界面
            Minecraft.getMinecraft().displayGuiScreen(new GuiQrLogin(this, key, resLoc));
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
