package net.fpsboost.util.network;

import net.fpsboost.util.Logger;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static net.fpsboost.Wrapper.mc;

public class WebUtil {

    // 发送 GET 请求并返回响应内容
    public static String get(String url) {
        return getRequest(url, false);
    }

    // 取消缓存的 GET 请求
    public static String getNoCache(String url) {
        return getRequest(url, true);
    }

    // 处理 GET 请求逻辑
    private static String getRequest(String url, boolean noCache) {
        try {
            HttpURLConnection connection = getHttpURLConnection(url);

            // 设置缓存控制
            if (noCache) {
                connection.setUseCaches(false);
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setRequestProperty("Pragma", "no-cache");
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    return reader.lines().collect(Collectors.joining("\n"));
                }
            } else {
                throw new IOException("HTTP request failed with response code: " + responseCode);
            }
        } catch (IOException e) {
            Logger.error("Error during HTTP request: {}", e.getMessage());
            return null;
        }
    }

    // 获取 HTTP 连接
    private static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");

        // Set browser-like headers
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        return connection;
    }

    /**
     * 从指定的 URL 获取图片，并模拟谷歌浏览器的请求头。
     *
     * @param imageUrl 图片的 URL
     * @return BufferedImage 图片对象
     * @throws IOException 如果发生网络或 IO 错误
     */
    public static BufferedImage fetchImage(String imageUrl) throws IOException {
        HttpURLConnection connection = getHttpURLConnection(imageUrl);

        // 设置连接和读取的超时时间为 5 秒
        connection.setConnectTimeout(5000); // 设置连接超时为 5 秒
        connection.setReadTimeout(5000); // 设置读取超时为 5 秒

        try {
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Failed to fetch image, HTTP response code: " + responseCode);
            }

            try (InputStream inputStream = connection.getInputStream()) {
                return ImageIO.read(inputStream);
            }
        } catch (IOException e) {
            // 如果是超时或其他 IO 错误，返回 null
            Logger.error("Failed to fetch image or timeout occurred: {}", e.getMessage());
            return null;
        }
    }

    // 根据给定 URL 和名称绑定纹理
    public static ResourceLocation bindTextureWithUrl(String url, String name) {
        return bindTexture(url, name, false);
    }

    // 根据给定文件路径和名称绑定本地纹理
    public static ResourceLocation bindLocalTexture(String filePath, String name) {
        return bindTexture(filePath, name, true);
    }

    // 绑定纹理的通用方法
    private static ResourceLocation bindTexture(String path, String name, boolean isLocal) {
        DynamicTexture dt;
        ResourceLocation res = new ResourceLocation(name);
        try {
            BufferedImage image = isLocal ? ImageIO.read(new File(path)) : fetchImage(path);
            assert image != null;
            dt = new DynamicTexture(image);
            mc.getTextureManager().loadTexture(res, dt);
            mc.getTextureManager().bindTexture(res);
        } catch (IOException e) {
            Logger.error("Failed to load texture: {}", e.getMessage());
        }
        return res;
    }
}
