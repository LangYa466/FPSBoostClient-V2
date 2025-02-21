package net.fpsboost.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fpsboost.config.Config;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.value.Value;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
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
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();
        ModuleManager.modules.forEach(module -> {
            JsonObject moduleObject = new JsonObject();

            moduleObject.addProperty("enable", module.enable);
            moduleObject.addProperty("key", module.keyCode);

            JsonObject valuesObject = getValueJsonObject(module);
            moduleObject.add("values", valuesObject);
            object.add(module.name, moduleObject);
        });
        return object;
    }

    private JsonObject getValueJsonObject(Module module) {
        JsonObject valuesObject = new JsonObject();
        module.values.forEach(value -> {
            if (value instanceof NumberValue) {
                valuesObject.addProperty(value.name, ((NumberValue) value).getValue());
            } else if (value instanceof BooleanValue) {
                valuesObject.addProperty(value.name, ((BooleanValue) value).getValue());
            } else if (value instanceof ModeValue) {
                valuesObject.addProperty(value.name, ((ModeValue) value).getValue());
            } else if (value instanceof ColorValue) {
                valuesObject.addProperty(value.name, ((ColorValue) value).getValue().toString());
            }
        });
        return valuesObject;
    }

    @Override
    public void loadConfig(JsonObject object) {
        ModuleManager.modules.stream()
                .filter(module -> object.getAsJsonObject(module.name) != null)
                .forEach(module -> loadModuleConfig(module, object.getAsJsonObject(module.name)));
    }

    private void loadModuleConfig(Module module, JsonObject moduleObject) {
        if (moduleObject.has("enable")) {
            module.setEnable(moduleObject.get("enable").getAsBoolean());
        }
        if (moduleObject.has("key")) {
            module.keyCode = moduleObject.get("key").getAsInt();
        }
        JsonObject valuesObject = moduleObject.getAsJsonObject("values");
        if (valuesObject != null) {
            module.values.forEach(value -> {
                if (valuesObject.has(value.name)) {
                    JsonElement theValue = valuesObject.get(value.name);
                    setValue(value, theValue);
                }
            });
        }
    }

    private void setValue(Value<?> value, JsonElement theValue) {
        if (value instanceof NumberValue) {
            ((NumberValue) value).setValue(theValue.getAsNumber().doubleValue());
        } else if (value instanceof BooleanValue) {
            ((BooleanValue) value).setValue(theValue.getAsBoolean());
        } else if (value instanceof ModeValue) {
            ((ModeValue) value).setValue(theValue.getAsString());
        } else if (value instanceof ColorValue) {
            ((ColorValue) value).setValue2(theValue.getAsString());
        }
    }
}
