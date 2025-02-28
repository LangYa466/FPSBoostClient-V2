package net.fpsboost.screen.musicPlayer;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import net.fpsboost.util.ChatUtil;
import net.fpsboost.util.Logger;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LangYa466
 * @since 2/28/2025
 */
public class MusicPlayer {
    public static long songStartTime = 0;

    private MediaPlayer mediaPlayer;
    private final ReentrantLock lock = new ReentrantLock();

    public MusicPlayer() {
        JavaFXInitializer.initialize();
    }

    public void play(String url) {
        new Thread(() -> {
            lock.lock();
            try {
                if (mediaPlayer != null) {
                    ChatUtil.addMessageWithClient("正在停止当前音乐...");
                    stop();
                }

                // 下载音乐到临时文件
                Path tempFile = Files.createTempFile("fps_boost_temp_music", ".mp3");
                ChatUtil.addMessageWithClient("正在下载音乐到: " + tempFile);
                Files.copy(new URL(url).openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

                // 运行在 JavaFX 线程
                Platform.runLater(() -> {
                    try {
                        ChatUtil.addMessageWithClient("加载音乐：" + tempFile.toUri());
                        Media media = new Media(tempFile.toUri().toString());
                        mediaPlayer = new MediaPlayer(media);

                        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);  // 设置为无限循环

                        mediaPlayer.setOnReady(() -> {
                            songStartTime = System.currentTimeMillis();
                            ChatUtil.addMessageWithClient("开始播放新音乐：" + url);
                            mediaPlayer.play();
                        });

                        mediaPlayer.setOnEndOfMedia(() -> {
                            ChatUtil.addMessageWithClient("音乐播放结束");
                            stop();
                        });

                        mediaPlayer.setOnError(() -> ChatUtil.addMessageWithClient("播放错误：" + mediaPlayer.getError()));

                        mediaPlayer.setVolume(1.0);  // 确保音量最大

                    } catch (Exception e) {
                        ChatUtil.addMessageWithClient("播放错误：" + e.getMessage());
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
    }

    public void pause() {
        Platform.runLater(() -> {
            lock.lock();
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    ChatUtil.addMessageWithClient("音乐暂停");
                }
            } finally {
                lock.unlock();
            }
        });
    }

    public void resume() {
        Platform.runLater(() -> {
            lock.lock();
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.play();
                    ChatUtil.addMessageWithClient("音乐继续播放");
                }
            } finally {
                lock.unlock();
            }
        });
    }

    public void stop() {
        Platform.runLater(() -> {
            lock.lock();
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    mediaPlayer = null;
                    ChatUtil.addMessageWithClient("音乐停止");
                }
            } finally {
                lock.unlock();
            }
        });
    }
}
