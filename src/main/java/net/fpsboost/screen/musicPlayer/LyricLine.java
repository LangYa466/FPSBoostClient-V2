package net.fpsboost.screen.musicPlayer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LangYa466
 * @since 2/28/2025
 */
@Getter
@AllArgsConstructor
public class LyricLine {
    private long time; // 单位：毫秒
    private String text;
}
