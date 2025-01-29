package net.fpsboost.value.impl;

import net.fpsboost.value.Value;

/**
 * @author LangYa
 * @since 2024/9/1 20:12
 */
public class NumberValue extends Value<Double> {

    public double maxValue;
    public double minValue;
    public double incValue;

    public NumberValue(String cnName,String name, double value, double maxValue, double minValue, double incValue) {
        super(cnName, name, value);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.incValue = incValue;
    }

    public NumberValue(String cnName,String name, int value, int maxValue, int minValue, double incValue) {
        super(cnName,name, (double) value);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.incValue = incValue;
    }

    public NumberValue(String cnName,String name, int value, int maxValue, int minValue, int incValue) {
        super(cnName,name, (double) value);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.incValue = incValue;
    }

    public void add() {
        setValue(getValue() + incValue);
    }

    public void cut() {
        double cutValue = getValue() - incValue;
        if (cutValue > minValue) return;
        setValue(cutValue);
    }
}
