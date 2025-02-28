package net.fpsboost.screen.musicPlayer;

import net.fpsboost.module.Module;

/**
 * @author LangYa466
 * @since 2/28/2025
 */
public class MusicPlayerModule extends Module {
    public MusicPlayerModule() {
        super("NetEase Cloud Music Player", "网易云音乐播放器");
    }

    @Override
    public void onEnable() {
        enable = false;
        mc.displayGuiScreen(new GuiNeteaseMusic());
        super.onEnable();
    }
}
