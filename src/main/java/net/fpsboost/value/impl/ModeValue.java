package net.fpsboost.value.impl;

import net.fpsboost.value.Value;

/**
 * @author LangYa
 * @since 2024/9/1 20:12
 */
public class ModeValue extends Value<String> {

    public final String[] modes;

    public ModeValue(String cnName, String name, String value, String... modes) {
        super(cnName, name, value);
        this.modes = modes;
    }

    public boolean isMode(String value) {
        return this.getValue().equals(value);
    }

    public void setNextValue() {
        setValue(getNextValue());
    }

    public String getNextValue() {
        if (modes.length == 0) return getValue(); // 确保不会返回 null

        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(this.getValue())) {
                return (i + 1 < modes.length) ? modes[i + 1] : modes[0]; // 如果超出范围，返回 modes[0]
            }
        }
        return modes[0]; // 找不到当前值，默认返回第一个模式
    }

    public void setPreviousValue() {
        setValue(getPreviousValue());
    }

    public String getPreviousValue() {
        if (modes.length == 0) return getValue(); // 确保不会返回 null

        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(this.getValue())) {
                return (i - 1 >= 0) ? modes[i - 1] : modes[modes.length - 1]; // 如果超出范围，返回 modes[modes.length - 1]
            }
        }
        return modes[modes.length - 1]; // 找不到当前值，默认返回最后一个模式
    }

    @Override
    public String getValue() {
        String value = super.getValue();
        if (value == null) return "Null(Error)";
        return value;
    }
}
