package net.fpsboost.util.network;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static net.fpsboost.Wrapper.mc;

public class WebUtil {
    
    // 发送 GET 请求并返回响应内容
    public static String get(String url) {
        try {
            HttpURLConnection connection = getHttpURLConnection(url);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Specify the character encoding
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line).append("\n");
                }
                reader.close();
                return responseBuilder.toString();
            }
            throw new IOException("HTTP request failed with response code: " + responseCode);
        }
        catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    // 取消缓存
    public static String getNoCache(String url) {
        try {
            HttpURLConnection connection = getHttpURLConnection(url);

            // 禁用缓存
            connection.setUseCaches(false);

            // 设置请求头，确保没有缓存
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Pragma", "no-cache");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // 指定字符编码
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line).append("\n");
                }
                reader.close();
                return responseBuilder.toString();
            }
            throw new IOException("HTTP request failed with response code: " + responseCode);
        }
        catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
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
        // 打开连接
        HttpURLConnection connection = getHttpURLConnection(imageUrl);

        // 检查 HTTP 响应码
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to fetch image, HTTP response code: " + responseCode);
        }

        // 读取图片并返回 BufferedImage
        return ImageIO.read(connection.getInputStream());
    }

    // 根据给定 URL 和名称绑定纹理
    public static ResourceLocation bindTextureWithUrl(String url,String name) {
        DynamicTexture dt;
        ResourceLocation res = new ResourceLocation(name);
        try {
            dt = new DynamicTexture(fetchImage(url));
            mc.getTextureManager().loadTexture(res, dt);
            mc.getTextureManager().bindTexture(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static ResourceLocation bindLocalTexture(String filePath, String name) {
        DynamicTexture dt;
        ResourceLocation res = new ResourceLocation(name);
        try {
            // 读取本地图片
            File file = new File(filePath);
            BufferedImage bufferedImage = ImageIO.read(file);
            dt = new DynamicTexture(bufferedImage);
            mc.getTextureManager().loadTexture(res, dt);
            mc.getTextureManager().bindTexture(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

}
