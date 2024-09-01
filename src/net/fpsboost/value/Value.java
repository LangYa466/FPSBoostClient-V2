package net.fpsboost.value;

/**
 * @author LangYa
 * @since 2024/9/1 20:07
 */
public class Value<T> {
    public String name;
    public T value;

    public Value(String name, T value) {
        this.name = name;
        this.value = value;
    }
}
