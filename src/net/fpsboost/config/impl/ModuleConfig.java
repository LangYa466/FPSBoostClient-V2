package net.fpsboost.config.impl;

import net.fpsboost.config.Config;
import net.fpsboost.element.Element;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.value.Value;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ModeValue;
import net.fpsboost.value.impl.NumberValue;

/**
 * @author LangYa
 * @since 2024/9/3 18:21
 */
public class ModuleConfig extends Config {

    public ModuleConfig() {
        super("Module");
    }

    @Override
    public void save() {
        for (Module module : ModuleManager.modules) {
            data.put(module.name + "-Enable", String.valueOf(module.enable));
            data.put(module.name + "-KeyCode", Integer.toString(module.keyCode));
            if (module.values.isEmpty()) return;
            for (Value<?> value : module.values) {
                if (value instanceof BooleanValue) {
                    data.put(value.name,String.valueOf(((BooleanValue)value).value));
                }
                if (value instanceof ModeValue) {
                    data.put(value.name,String.valueOf(((ModeValue)value).value));
                }
                if (value instanceof NumberValue) {
                    data.put(value.name,Double.toString(((NumberValue)value).value));
                }
            }
        }
    }

    @Override
    public void load() {
        for (Module module : ModuleManager.modules) {
            module.enable = Boolean.parseBoolean(data.get(module.name + "-Enable"));
            module.keyCode = Integer.parseInt(data.get(module.name + "-KeyCode"));
            if (module.values.isEmpty()) return;
            for (Value<?> value : module.values) {
                if (value instanceof BooleanValue) {
                    ((BooleanValue)value).value = Boolean.parseBoolean(data.get(value.name));
                }
                if (value instanceof ModeValue) {
                    ((ModeValue)value).value = String.valueOf(data.get(value.name));
                }
                if (value instanceof NumberValue) {
                    ((NumberValue)value).value = Double.parseDouble(data.get(value.name));
                }
            }
        }
    }

}
