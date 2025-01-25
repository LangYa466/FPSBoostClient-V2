package net.fpsboost.util;

import cn.langya.Logger;
import net.fpsboost.Wrapper;
import net.fpsboost.config.ConfigManager;
import net.fpsboost.module.impl.ClientSettings;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.net.URL;

/**
 * @author LangYa
 * @since 2024/11/24 22:52
 */
public class CapeUtil implements Wrapper {
    public static ResourceLocation cape = null;
    public static final File oldCapeFile = new File(ConfigManager.dir,"FPSBoostCape.data");

    public static void init() {
        if (oldCapeFile.exists()) if (readFromFile(oldCapeFile) != "") setCape(readFromFile(oldCapeFile));
    }

    public static void setCape(String url) {
        if (url.contains("https://littleskin.cn/skinlib/show/")) url = "https://littleskin.cn/raw/" + url.replace("https://littleskin.cn/skinlib/show/","");
        if (!oldCapeFile.exists()) {
            try {
                oldCapeFile.createNewFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,(ClientSettings.isChinese ?"Create cape verification failed":"创建披风验证失败"));
                Logger.error(e.getMessage());
                JOptionPane.showMessageDialog(null,(ClientSettings.isChinese ?"Take a screenshot and send it to the administrator":"截图给群主 让他给你补卡密 因为客户端写入出现了验证错误!!"));
                return;
            }
            if (!writeToFile(oldCapeFile,url)) JOptionPane.showMessageDialog(null,(ClientSettings.isChinese ?"Take a screenshot and send it to the administrator":"截图给群主 让他给你补卡密 因为客户端写入出现了验证错误!!"));
        }
        DynamicTexture dt;
        try {
            dt = new DynamicTexture(ImageIO.read(new URL(url)));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,(ClientSettings.isChinese ?"Failed to load cape image and input again: ":"读取披风图片失败 请重新输入 按确定重新输入"));
            Logger.error(e.getMessage());
            setCape(JOptionPane.showInputDialog((ClientSettings.isChinese ?"New cape image link":"新的披风图片链接")));
            return;
        }
        ResourceLocation capeRes = new ResourceLocation("clientCape");
        mc.getTextureManager().loadTexture(capeRes, dt);
        mc.getTextureManager().bindTexture(capeRes);

        cape = capeRes;
    }

    public static boolean writeToFile(File file, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println((ClientSettings.isChinese ?"File writing failed: ":"写入文件时出错: ") + e.getMessage());
            return false;
        }
    }

    public static String readFromFile(File file) {
        if (!file.exists()) {
            System.err.println((ClientSettings.isChinese ?"File not found: ":"文件不存在：") + file.getAbsolutePath());
            return "";
        }

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        } catch (IOException e) {
            System.err.println((ClientSettings.isChinese ? "Reading file failed: ":"读取文件时出错: ") + e.getMessage());
            return "";
        }
    }
}
