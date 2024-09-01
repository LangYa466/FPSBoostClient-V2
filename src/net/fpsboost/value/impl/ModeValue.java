package net.fpsboost.value.impl;

import net.fpsboost.value.Value;

/**
 * @author LangYa
 * @since 2024/9/1 20:12
 */
public class ModeValue extends Value<String> {

    public String[] modes;

    public ModeValue(String name,String value, String... modes) {
        super(name, value);
        this.modes = modes;
    }

    public boolean isMode(String value) {
        return this.value.equals(value);
    }

}
