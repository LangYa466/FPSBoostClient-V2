package net.fpsboost.module;

import net.fpsboost.Wrapper;

/**
 * @author LangYa
 * @since 2024/8/30 21:19
 */
public class Module implements Wrapper {
    public final String name,cnName,description;
    public boolean enable;
    public int keyCode;

    public Module(String name,String cnName,String description) {
        this.name = name;
        this.cnName = cnName;
        this.description = description;
    }

    public void onEnable() { }
    public void onDisable() { }

    public void toggle() {
        this.enable = !enable;
        if (enable) onEnable(); else onDisable();
    }

    public void onRender2D() { }
}
