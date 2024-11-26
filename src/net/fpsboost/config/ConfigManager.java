package net.fpsboost.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParser;
import net.fpsboost.Client;
import net.fpsboost.Wrapper;
import net.fpsboost.config.impl.ElementConfig;
import net.fpsboost.config.impl.*;
import net.fpsboost.module.impl.ClientSettings;
import org.apache.commons.io.FileUtils;

/**
 * 配置管理器类，实现了 Wrapper 接口
 * 负责管理模块的配置加载和保存
 */
public class ConfigManager implements Wrapper {
    private static final List<Config> configs = new ArrayList<>();
    public static final File dir = new File(mc.mcDataDir, Client.name);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean isFirst;

    /**
     * 初始化配置管理器
     * 创建配置目录并加载所有配置
     */
    public static void init() {
        if (!dir.exists()) {
            dir.mkdir();
            isFirst = true;
        }

        configs.add(new ModuleConfig());
        configs.add(new ElementConfig());
        loadAllConfig();
    }

    /**
     * 加载指定名称的配置文件
     *
     * @param name 配置文件名称
     */
    public static void loadConfig(String name) {
        File file = new File(dir, name);
        JsonParser jsonParser = new JsonParser();
        if (file.exists()) {
            for (Config config : configs) {
                if (!config.name.equals(name)) continue;
                try {
                    config.loadConfig(jsonParser.parse(new FileReader(file)).getAsJsonObject());
                    if (ClientSettings.INSTANCE.cnMode.getValue()){
                        System.out.println("加载客户端配置: " + name);
                    }else{
                        System.out.println("Loading client config: " + name);
                    }
                }
                catch (FileNotFoundException e) {
                    if (ClientSettings.INSTANCE.cnMode.getValue()) {
                        System.out.println("配置文件不存在: " + name);
                    }else {
                        System.out.println("Failed to load config: " + name);

                    }
                    e.printStackTrace();
                }
                break;
            }
        } else {
            System.out.println("Config " + name + " doesn't exist, creating a new one...");
            saveConfig(name);
        }
    }

    /**
     * 保存指定名称的配置文件
     *
     * @param name 配置文件名称
     */
    public static void saveConfig(String name) {
        File file = new File(dir, name);
        try {
            if (ClientSettings.INSTANCE.cnMode.getValue()) {
                System.out.println("保存客户端配置: " + name);
            }else {
                System.out.println("Saving client config: " + name);
            }
            file.createNewFile();
            for (Config config : configs) {
                if (!config.name.equals(name)) continue;
                FileUtils.writeByteArrayToFile(file, gson.toJson(config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                break;
            }
        }
        catch (IOException e) {
            System.out.println("Failed to save config: " + name);
        }
    }

    /**
     * 加载所有配置文件
     */
    public static void loadAllConfig() {
        configs.forEach(it -> loadConfig(it.name));
        if (ClientSettings.INSTANCE.cnMode.getValue()) {
            System.out.println("成功加载全部配置");
        }else {
            System.out.println("Successfully loaded all configs");
        }
    }

    /**
     * 保存所有配置文件
     */
    public static void saveAllConfig() {
        configs.forEach(it -> saveConfig(it.name));
        if (ClientSettings.INSTANCE.cnMode.getValue()) {
            System.out.println("成功保存全部配置");
        }
        else {
            System.out.println("Successfully saved all configs");
        }
    }
}
