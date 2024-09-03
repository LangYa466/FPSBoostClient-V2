package net.fpsboost.config;

import java.io.File;

/**
 * @author LangYa
 * @since 2024/9/3 17:47
 */
public class Config {
    public File dir = ConfigManager.configDir;

    public String name;
    public File file;
    public Data data;

    public Config(String name) {
        this.name = name;
        this.file = new File(dir,name + ".data");
        this.data = new Data();
    }

    public void save() {}

    public void load() { }

}
