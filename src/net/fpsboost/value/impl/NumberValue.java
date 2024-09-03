package net.fpsboost.value.impl;

import net.fpsboost.value.Value;

/**
 * @author LangYa
 * @since 2024/9/1 20:12
 */
public class NumberValue extends Value<Double> {

    public Double maxValue;
    public Double minValue;
    public Double incValue;

    public NumberValue(String name, Double value, Double maxValue, Double minValue, Double incValue) {
        super(name, value);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.incValue = incValue;
    }

}
