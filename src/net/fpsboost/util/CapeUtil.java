package net.fpsboost.util;

import dev.jnic.annotations.JNICInclude;
import net.fpsboost.Wrapper;
import net.fpsboost.config.ConfigManager;
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
@JNICInclude
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
                JOptionPane.showMessageDialog(null,"创建披风验证失败");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,"截图给群主 让他给你补卡密 因为客户端写入出现了验证错误!!");
                return;
            }
            if (!writeToFile(oldCapeFile,url)) JOptionPane.showMessageDialog(null,"截图给群主 让他给你补卡密 因为客户端写入出现了验证错误!!");
        }
        DynamicTexture dt;
        try {
            dt = new DynamicTexture(ImageIO.read(new URL(url)));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"读取披风图片失败 请重新输入 按确定重新输入");
            e.printStackTrace();
            setCape(JOptionPane.showInputDialog("新的披风图片链接"));
            return;
        }
        ResourceLocation capeRes = new ResourceLocation("clientCape");
        mc.getTextureManager().loadTexture(capeRes, dt);
        mc.getTextureManager().bindTexture(capeRes);

        cape = capeRes;
        IRCUtil.transport.sendCapeURL(url);
    }

    public static boolean writeToFile(File file, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
            return false;
        }
    }

    public static String readFromFile(File file) {
        if (!file.exists()) {
            System.err.println("文件不存在：" + file.getAbsolutePath());
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
            System.err.println("读取文件时出错: " + e.getMessage());
            return "";
        }
    }
}
