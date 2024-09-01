package net.fpsboost.value.impl;

import net.fpsboost.value.Value;

/**
 * @author LangYa
 * @since 2024/9/1 20:12
 */
public class NumberValue extends Value<Number> {

    public Number maxValue;
    public Number minValue;
    public Number incValue;

    public NumberValue(String name, Number value, Number maxValue, Number minValue, Number incValue) {
        super(name, value);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.incValue = incValue;
    }

}
