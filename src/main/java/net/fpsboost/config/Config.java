package net.fpsboost.config;

import com.google.gson.JsonObject;

public abstract class Config {
    public final String name;

    public Config(final String name) {
        this.name = name + ".json";
    }

    public abstract JsonObject saveConfig();

    public abstract void loadConfig(JsonObject jsonObject);
}