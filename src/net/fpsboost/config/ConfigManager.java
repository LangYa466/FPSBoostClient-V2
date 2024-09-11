package net.fpsboost.config;

import com.google.gson.*;
import net.fpsboost.Client;
import net.fpsboost.Wrapper;
import net.fpsboost.config.impl.*;

import java.io.*;
import java.util.ArrayList;

/**
 * @author LangYa
 * @since 2024/9/3 17:47
 */
public class ConfigManager implements Wrapper {
    public static final ArrayList<Config> configs = new ArrayList<>();
    public static final File clientDir = new File(mc.mcDataDir, Client.name);
    public static final File configDir = new File(clientDir,"configs");
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void init() {
        if (!clientDir.exists()) clientDir.mkdir();
        if (!configDir.exists()) configDir.mkdir();
        configs.add(new ModuleConfig());
        configs.add(new ElementConfig());

        loadAll();
    }

    public static void save(String name) {
        System.out.printf("[ConfigManager] Saved %sConfig%n",name);
        for (Config config : configs) {
            if (config.name.equals(name)) {
                config.save();
                try {
                    config.data.saveToFile(config.file.getAbsolutePath());
                } catch (IOException e) {
                    System.out.printf("Config %s error : %s%n",config.name,e.getMessage());
                }
            }
        }
    }

    public static void saveAll() {
        configs.forEach(config -> save(config.name));
    }

    public static void load(String name) {
        System.out.printf("[ConfigManager] Loaded %sConfig%n",name);
        for (Config config : configs) {
            if (!config.name.equals(name)) continue;
            if (config.file.exists()) {
                try {
                    config.data.loadFromFile(config.file.getAbsolutePath());
                    config.load();
                } catch (IOException e) {
                    System.out.printf("Config %s error : %s",config.name,e.getMessage());
                }
            }
        }
    }

    public static void loadAll() {
       configs.forEach(config -> load(config.name));
    }
}
