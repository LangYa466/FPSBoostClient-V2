package net.fpsboost.value;

/**
 * @author LangYa
 * @since 2024/9/1 20:07
 */
public class Value<T> {
    public String name;
    private T value;
    public boolean isHide = false;

    public Value(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        onEditValue();
    }

    public void onEditValue() { }
}
