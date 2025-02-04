package net.fpsboost.module;

import net.fpsboost.Wrapper;
import net.fpsboost.handler.MessageHandler;
import net.fpsboost.module.impl.ClientSettings;
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

    public ArrayList<Value<?>> values = new ArrayList<>();;

    public Module(String name,String cnName,String description,String cnDescription,int keyCode) {
        this.name = name;
        this.cnName = cnName;
        this.cnDescription = cnDescription;
        this.description = description;
        this.keyCode = keyCode;
    }

    public Module(String name,String cnName,String description,String cnDescription) {
        this.name = name;
        this.cnName = cnName;
        this.cnDescription = cnDescription;
        this.description = description;
    }

    public Module(String name,String cnName) {
        this.name = name;
        this.cnName = cnName;
        this.cnDescription = "";
        this.description = "";
    }

    public void onEnable() { }
    public void onDisable() { }

    public void toggle() {
        setEnable(!enable);
    }

    public void setEnable(boolean enable) {
        setEnable(enable, false);
    }
    public void setEnable(boolean enable,boolean silent) {
        this.enable = enable;
        if (enable) {
            onEnable();
        } else {
            onDisable();
        }

        if (silent) {
            if (mc.thePlayer != null) MessageHandler.addMessage(String.format("%s %s", getDisplayName(), enable ? "已开启" : "已关闭"), MessageHandler.MessageType.Info);
        }
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

    public String getDisplayName() {
        return ClientSettings.isChinese ? cnName : name;
    }
}
