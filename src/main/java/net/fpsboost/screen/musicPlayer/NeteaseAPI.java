package net.fpsboost.screen.musicPlayer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;
import net.fpsboost.Wrapper;
import net.fpsboost.util.Logger;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;

/**
 * @author LangYa466
 * @since 2/28/2025
 */
public class NeteaseAPI implements Wrapper {
    private String baseUrl = "https://music.skidder.top";
    private final Gson gson = new Gson();

    // GET 请求辅助方法
    private String httpGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    // POST 请求辅助方法
    private String httpPost(String urlStr, String params) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    // 获取二维码 key 接口
    public String getQrKey() {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String response = httpGet(baseUrl + "/login/qr/key?timestamp=" + timestamp);
            JsonObject json = gson.fromJson(response, JsonObject.class);
            return json.getAsJsonObject("data").get("unikey").getAsString();
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }
    }

    // 获取二维码图片接口
    public String getQrCodeImage(String key) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String response = httpGet(baseUrl + "/login/qr/create?key=" + key + "&qrimg=true&timestamp=" + timestamp);
            JsonObject json = gson.fromJson(response, JsonObject.class);
            return json.getAsJsonObject("data").get("qrimg").getAsString();
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }
    }

    // 检测二维码状态接口
    public int checkQrCodeStatus(String key) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String response = httpGet(baseUrl + "/login/qr/check?key=" + key + "&timestamp=" + timestamp);
            JsonObject json = gson.fromJson(response, JsonObject.class);
            return json.get("code").getAsInt();
        } catch (Exception e) {
            Logger.error(e);
            return -1;
        }
    }

    // 搜索歌曲接口
    public List<Song> search(String keyword) {
        List<Song> songs = new ArrayList<>();
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());
            String response = httpGet(baseUrl + "/search?keywords=" + encodedKeyword);
            JsonObject json = gson.fromJson(response, JsonObject.class);
            if (!json.has("result")) return songs;
            JsonObject result = json.getAsJsonObject("result");
            if (!result.has("songs")) return songs;
            JsonArray songsArray = result.getAsJsonArray("songs");
            for (JsonElement songElement : songsArray) {
                JsonObject songJson = songElement.getAsJsonObject();
                String id = songJson.get("id").getAsString();
                String name = songJson.get("name").getAsString();
                String artist = "未知";
                if (songJson.has("artists")) {
                    JsonArray artists = songJson.getAsJsonArray("artists");
                    if (!artists.isEmpty()) {
                        artist = artists.get(0).getAsJsonObject().get("name").getAsString();
                    }
                }
                songs.add(new Song(id, name, artist));
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return songs;
    }

    // 获取歌曲播放地址接口
    public String getSongUrl(String songId) {
        try {
            String response = httpGet(baseUrl + "/song/url?id=" + songId);
            JsonObject json = gson.fromJson(response, JsonObject.class);
            JsonArray data = json.getAsJsonArray("data");
            if (!data.isEmpty()) {
                return data.get(0).getAsJsonObject().get("url").getAsString();
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }

    // 获取歌词接口
    public List<LyricLine> getLyric(String songId) {
        try {
            String response = httpGet(baseUrl + "/lyric?id=" + songId);
            JsonObject json = gson.fromJson(response, JsonObject.class);
            if (!json.has("lrc")) return new ArrayList<>();
            JsonObject lrcObj = json.getAsJsonObject("lrc");
            String lyricStr = lrcObj.get("lyric").getAsString();
            return LyricsParser.parse(lyricStr);
        } catch (Exception e) {
            Logger.error(e);
        }
        return new ArrayList<>();
    }

    // 获取歌曲封面图接口
    public ResourceLocation getSongCover(String songId) {
        try {
            String response = httpGet(baseUrl + "/song/detail?ids=" + songId);
            JsonObject json = gson.fromJson(response, JsonObject.class);
            JsonArray songs = json.getAsJsonObject("songs").getAsJsonArray("songs");
            if (!songs.isEmpty()) {
                String imageUrl = songs.get(0).getAsJsonObject().getAsJsonObject("al").get("picUrl").getAsString();
                BufferedImage image = ImageIO.read(new URL(imageUrl));
                DynamicTexture dynamicTexture = new DynamicTexture(image);
                ResourceLocation resourceLocation = new ResourceLocation("custom_textures", "net_image");
                mc.getTextureManager().loadTexture(resourceLocation, dynamicTexture);
                mc.getTextureManager().bindTexture(resourceLocation);
                return resourceLocation;
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }
}
