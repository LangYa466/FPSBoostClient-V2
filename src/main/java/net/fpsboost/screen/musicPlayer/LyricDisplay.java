package net.fpsboost.screen.musicPlayer;

import net.fpsboost.element.Element;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.List;

/**
 * @author LangYa466
 * @since 2/28/2025
 */
public class LyricDisplay extends Element {
    private static List<LyricLine> lyricLines;
    private static boolean enabled = false;

    public LyricDisplay() {
        super("LyricDisplay", "歌词显示");
    }

    public static void setLyrics(List<LyricLine> lyrics) {
        lyricLines = lyrics;
        enabled = (lyrics != null && !lyrics.isEmpty());
    }

    @Override
    public void onDraw() {
        if (!enabled || lyricLines == null || lyricLines.isEmpty()) return;
        long currentTime = System.currentTimeMillis() - MusicPlayer.songStartTime;
        int currentIndex = -1;
        for (int i = 0; i < lyricLines.size(); i++) {
            if (currentTime >= lyricLines.get(i).getTime()) {
                currentIndex = i;
            } else {
                break;
            }
        }
        width = 50;
        height = 50;

        // anti bug
        if (currentIndex < 0 || currentIndex >= lyricLines.size()) return;

        String lyric = lyricLines.get(currentIndex).getText();
        String currentLyric = lyric.isEmpty() ? "........" : lyric;
        String nextLyric = (currentIndex + 1 < lyricLines.size()) ? lyricLines.get(currentIndex + 1).getText() : "结束";
        FontRenderer fontRenderer = FontManager.client(28);

        String widthString = (currentLyric.length() >= nextLyric.length()) ? currentLyric : nextLyric;

        width = fontRenderer.getStringWidth(widthString) + 5;
        height = fontRenderer.getHeight() * 2 + 3;
        fontRenderer.drawStringWithShadow(currentLyric, 0, 0, Color.orange.getRGB());
        fontRenderer.drawStringWithShadow(nextLyric, 0, fontRenderer.getHeight() + 3, -1);
        super.onDraw();
    }
}