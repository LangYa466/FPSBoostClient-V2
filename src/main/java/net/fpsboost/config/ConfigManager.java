package net.fpsboost.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.fpsboost.Client;
import net.fpsboost.Wrapper;
import net.fpsboost.config.impl.ElementConfig;
import net.fpsboost.config.impl.ModuleConfig;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.util.Logger;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author LangYa466
 * @since 2/11/2025
 */
public class ConfigManager implements Wrapper {
    private static final List<Config> configs = new ArrayList<>();
    public static final File dir = new File(mc.mcDataDir, Client.name);
    public static final File logsDir = new File(dir, "logs");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean isFirst;

    // 待保存的配置列表，线程安全
    private static final CopyOnWriteArrayList<String> pendingConfigs = new CopyOnWriteArrayList<>();
    private static final Thread saveThread = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(5000);
                if (!pendingConfigs.isEmpty()) {
                    for (String name : new ArrayList<>(pendingConfigs)) {
                        saveConfig(name);
                        pendingConfigs.remove(name);
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }, "ConfigSaveThread");

    /**
     * 初始化配置管理器
     * 创建配置目录并加载所有配置
     */
    public static void init() {
        if (!dir.exists()) {
            dir.mkdir();
            isFirst = true;
        }

        if (!logsDir.exists()) {
            logsDir.mkdir();
        }

        configs.add(new ModuleConfig());
        configs.add(new ElementConfig());

        saveThread.setDaemon(true);
        saveThread.start(); // 启动配置保存线程

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
                    logConfigAction(name, "加载客户端配置", "Loading client config");
                } catch (FileNotFoundException e) {
                    logConfigError(name, e);
                }
                break;
            }
        } else {
            Logger.warn("Config " + name + " doesn't exist, creating a new one...");
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
            // 确保文件存在
            if (!file.exists()) {
                file.createNewFile();
            }

            // 写入 JSON
            try (FileWriter writer = new FileWriter(file)) {
                for (Config config : configs) {
                    if (!config.name.equals(name)) continue;
                    writer.write(gson.toJson(config.saveConfig()));
                    logConfigAction(name, "保存客户端配置", "Saving client config");
                    break;
                }
            }
        } catch (IOException e) {
            Logger.error("Failed to save config: " + name);
            Logger.error(e.getMessage());
        }
    }

    /**
     * 加载所有配置文件
     */
    public static void loadAllConfig() {
        configs.forEach(it -> loadConfig(it.name));
        logConfigAction("全部配置", "成功加载全部配置", "Successfully loaded all configs");
    }

    /**
     * 保存所有配置文件
     */
    public static void saveAllConfig() {
        configs.forEach(it -> queueConfigForSave(it.name));
    }

    /**
     * 将配置加入待保存队列，5秒后自动保存
     *
     * @param name 配置名称
     */
    public static void queueConfigForSave(String name) {
        if (!pendingConfigs.contains(name)) {
            pendingConfigs.add(name);
        }
    }

    /**
     * 打印日志用于加载或保存配置的动作
     *
     * @param name      配置名称
     * @param cnMessage 中文日志消息
     * @param enMessage 英文日志消息
     */
    private static void logConfigAction(String name, String cnMessage, String enMessage) {
        if (ClientSettings.isChinese) {
            Logger.info(cnMessage + ": " + name);
        } else {
            Logger.info(enMessage + ": " + name);
        }
    }

    /**
     * 打印配置加载或保存的错误日志
     *
     * @param name 配置名称
     * @param e    异常对象
     */
    private static void logConfigError(String name, Exception e) {
        if (ClientSettings.isChinese) {
            Logger.error("配置文件不存在" + ": " + name);
        } else {
            Logger.error("Failed to load config" + ": " + name);
        }
        Logger.error(e.getMessage());
    }
}
