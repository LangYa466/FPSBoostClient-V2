package net.fpsboost.value.impl;

import net.fpsboost.value.Value;

/**
 * @author LangYa
 * @since 2024/9/1 20:11
 */
public class BooleanValue extends Value<Boolean> {

    public BooleanValue(String cnName, String name, Boolean value) {
        super(cnName,name, value);
    }

    public void toggle() {
        this.setValue(!getValue());
    }
}
