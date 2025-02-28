package net.fpsboost.screen.musicPlayer;

import net.fpsboost.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LangYa466
 * @since 2/28/2025
 */
public class LyricsParser {
    /**
     * 解析形如：[mm:ss.xxx] 歌词文本 的歌词字符串
     */
    public static List<LyricLine> parse(String lyricStr) {
        List<LyricLine> lines = new ArrayList<>();
        // 正则表达式匹配时间戳和歌词内容
        Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2}\\.\\d{3})\\](.*)");
        String[] arr = lyricStr.split("\n");
        for (String line : arr) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                try {
                    int minutes = Integer.parseInt(matcher.group(1));
                    float seconds = Float.parseFloat(matcher.group(2));
                    long timeMs = (long) ((minutes * 60 + seconds) * 1000);
                    String text = matcher.group(3).trim();
                    lines.add(new LyricLine(timeMs, text));
                } catch (Exception e) {
                    Logger.error(e);
                }
            }
        }
        return lines;
    }
}
