package net.fpsboost.value;

import lombok.Getter;

/**
 * @author LangYa
 * @since 2024/9/1 20:07
 */
public class Value<T> {
    public String name;
    @Getter
    private T value;
    public boolean isHide = false;

    public Value(String name, T value) {
        this.name = name;
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
}
