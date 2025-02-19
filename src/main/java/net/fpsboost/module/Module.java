package net.fpsboost.module;

import lombok.Getter;
import lombok.Setter;
import net.fpsboost.Wrapper;
import net.fpsboost.handler.MessageHandler;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.value.Value;

import java.util.ArrayList;
import java.util.List;

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
    @Setter
    @Getter
    public int keyCode;

    public final List<Value<?>> values = new ArrayList<>();

    // 统一构造函数，避免重复代码
    public Module(String name, String cnName, String description, String cnDescription, int keyCode) {
        this.name = name;
        this.cnName = cnName;
        this.description = description != null ? description : "";
        this.cnDescription = cnDescription != null ? cnDescription : "";
        this.keyCode = keyCode;
    }

    public Module(String name, String cnName, String description, String cnDescription) {
        this(name, cnName, description, cnDescription, 0);
    }

    public Module(String name, String cnName) {
        this(name, cnName, "", "", 0);
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onRender2D() {}
    public void onUpdate() {}
    public void onRender3D() {}
    public void onWorldLoad() {}

    public void toggle() {
        setEnable(!enable);
    }

    public void setEnable(boolean enable) {
        setEnable(enable, false);
    }

    public void setEnable(boolean enable, boolean silent) {
        this.enable = enable;
        if (enable) onEnable();
        else onDisable();

        // 消息通知逻辑优化，避免重复 if 判断
        if (silent && mc.thePlayer != null) {
            String status = enable ? "已开启" : "已关闭";
            MessageHandler.addMessage(getDisplayName() + " " + status, MessageHandler.MessageType.Info);
        }
    }

    public boolean isEnabled() {
        return enable;
    }

    public String getDisplayName() {
        return ClientSettings.isChinese ? cnName : name;
    }

}
