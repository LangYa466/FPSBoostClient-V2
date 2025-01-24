package net.fpsboost.module;

import net.fpsboost.Wrapper;
import net.fpsboost.handler.MessageHandler;
import net.fpsboost.value.Value;

import java.util.ArrayList;

/**
 * @author LangYa
 * @since 2024/8/30 21:19
 */
public class Module implements Wrapper {
    public final String name;
    public final String cnName;
    public String description;
    public String cnDescription;
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
        setEnable(!enable);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        boolean isNull = mc.thePlayer == null;
        if (enable) {
            onEnable();
        } else {
            onDisable();
        }
        if (!isNull) MessageHandler.addMessage(String.format("%s %s",cnName,enable ? "已开启" : "已关闭"),MessageHandler.MessageType.Info);
    }

    public void onRender2D() { }
    public void onUpdate() { }
    public void onRender3D() { }
    public void onWorldLoad() { }

    public boolean isEnabled() {
        return enable;
    }

    public String name() {
        return name;
    }
}
