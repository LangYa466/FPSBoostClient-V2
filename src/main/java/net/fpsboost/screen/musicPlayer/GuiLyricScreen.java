package net.fpsboost.screen.musicPlayer;

import net.fpsboost.util.font.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * @author LangYa466
 * @since 2/28/2025
 */
public class GuiLyricScreen extends GuiScreen {
    private List<LyricLine> lyricLines;
    private int centerX;
    private int centerY;

    public GuiLyricScreen(List<LyricLine> lyricLines) {
        this.lyricLines = lyricLines;
    }

    @Override
    public void initGui() {
        centerX = width / 2;
        centerY = height / 2;
        buttonList.clear();
        // 添加返回按钮 退出歌词显示界面
        buttonList.add(new GuiButton(0, centerX - 50, height - 40, 100, 20, "返回"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontManager.client().drawCenteredString("歌词", width / 2F, height / 2F - 20, -1);

        // 计算当前播放进度(单位：毫秒)
        long currentTime = System.currentTimeMillis() - MusicPlayer.songStartTime;

        // 查找当前应显示的歌词行
        int currentIndex = -1;
        for (int i = 0; i < lyricLines.size(); i++) {
            if (currentTime >= lyricLines.get(i).getTime()) {
                currentIndex = i;
            } else {
                break;
            }
        }

        // 设置显示范围(共显示 7 行：当前行及上下各 3 行)
        int linesToShow = 7;
        int startIndex = Math.max(0, currentIndex - 3);
        int endIndex = Math.min(lyricLines.size(), startIndex + linesToShow);

        int y = centerY - (linesToShow * 10) / 2;
        for (int i = startIndex; i < endIndex; i++) {
            String text = lyricLines.get(i).getText();
            int color = (i == currentIndex) ? 0xFFFF00 : -1; // 当前行高亮显示为黄色
            drawCenteredString(FontManager.client(), text, centerX, y, color);
            y += 10;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) {
        if (button.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        return null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
