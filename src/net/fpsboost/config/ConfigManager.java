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
import org.apache.commons.io.FileUtils;

public class ConfigManager implements Wrapper {
    private static final List<Config> configs = new ArrayList<>();
    public static final File dir = new File(mc.mcDataDir, Client.name);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean isFirst;

    public static void init() {
        if (!dir.exists()) {
            dir.mkdir();
            isFirst = true;
        }

        configs.add(new ModuleConfig());
        configs.add(new ElementConfig());
        loadAllConfig();
    }

    public static void loadConfig(String name) {
        File file = new File(dir, name);
        JsonParser jsonParser = new JsonParser();
        if (file.exists()) {
            System.out.println("加载客户端配置: " + name);
            for (Config config : configs) {
                if (!config.name.equals(name)) continue;
                try {
                    config.loadConfig(jsonParser.parse(new FileReader(file)).getAsJsonObject());
                }
                catch (FileNotFoundException e) {
                    System.out.println("Failed to load config: " + name);
                    e.printStackTrace();
                }
                break;
            }
        } else {
            System.out.println("Config " + name + " doesn't exist, creating a new one...");
            saveConfig(name);
        }
    }

    public static void saveConfig(String name) {
        File file = new File(dir, name);
        try {
            System.out.println("保存客户端配置: " + name);
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

    public static void loadAllConfig() {
        configs.forEach(it -> loadConfig(it.name));
        System.out.println("成功加载全部配置");
    }

    public static void saveAllConfig() {
        configs.forEach(it -> saveConfig(it.name));
        System.out.println("成功保存全部配置");
    }
}