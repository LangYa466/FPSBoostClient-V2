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

    public String getNextValue() {
        // Find the index of the current value
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(this.value)) {
                // Return the next mode in the array, or the first one if at the end
                return modes[(i + 1) % modes.length];
            }
        }
        // If the current value is not found in the modes array, return null or handle as needed
        return null;
    }
}
