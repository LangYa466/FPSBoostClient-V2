package net.fpsboost.value;

import lombok.Getter;
import net.fpsboost.module.impl.ClientSettings;

/**
 * @author LangYa
 * @since 2024/9/1 20:07
 */
public class Value<T> {
    public String name;
    public String cnName;
    @Getter
    private T value;
    public boolean isHide = false;

    public Value(String cnName,String name, T value) {
        this.name = name;
        this.cnName = cnName;
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
        onEditValue();
    }

    public void onEditValue() { }

    public void setHide(boolean hide) {
        this.isHide = hide;
    }

    public String getName() {
        if (ClientSettings.INSTANCE.cnMode.getValue()) return cnName; else return name;
    }
}
