package net.fpsboost.module;

import net.fpsboost.Wrapper;
import net.fpsboost.value.Value;

import java.util.ArrayList;

/**
 * @author LangYa
 * @since 2024/8/30 21:19
 */
public class Module implements Wrapper {
    public final String name,cnName,description,cnDescription;
    public boolean enable;
    public int keyCode;

    public ArrayList<Value<?>> values;
    public boolean inArray = true;

    public Module(String name,String cnName,String description,String cnDescription,int keyCode) {
        this.name = name;
        this.cnName = cnName;
        this.cnDescription = cnDescription;
        this.description = description;
        this.keyCode = keyCode;
        values = new ArrayList<>();
    }

    public Module(String name,String cnName,String description,String cnDescription) {
        this.name = name;
        this.cnName = cnName;
        this.cnDescription = cnDescription;
        this.description = description;
        values = new ArrayList<>();
    }

    public Module(String name,String cnName) {
        this.name = name;
        this.cnName = cnName;
        this.cnDescription = "";
        this.description = "";
        values = new ArrayList<>();
    }

    public void onEnable() { }
    public void onDisable() { }

    public void toggle() {
        this.enable = !enable;
        if (enable) onEnable(); else onDisable();
    }

    public void onRender2D() { }
    public void onUpdate() { }
    public void onRender3D() { }
    public void onWorldLoad() { }
}
