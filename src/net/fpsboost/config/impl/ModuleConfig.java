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

    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();
        for (Module module : ModuleManager.getAllModules()) {
            JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("enable", module.enable);
            moduleObject.addProperty("key", module.keyCode);
            JsonObject valuesObject = getValueJsonObject(module);
            moduleObject.add("values", valuesObject);
            object.add(module.name, moduleObject);
        }
        return object;
    }

    private JsonObject getValueJsonObject(Module module) {
        JsonObject valuesObject = new JsonObject();
        for (Value<?> value : module.values) {
            if (value instanceof NumberValue) {
                valuesObject.addProperty(value.name, ((NumberValue)value).getValue());
            }
            else if (value instanceof BooleanValue) {
                valuesObject.addProperty(value.name, ((BooleanValue)value).getValue());
            }
            else if (value instanceof ModeValue) {
                valuesObject.addProperty(value.name, ((ModeValue)value).getValue());
            }
            else if (value instanceof ColorValue) {
                valuesObject.addProperty(value.name, ((ColorValue)value).getValue());
            }
        }
        return valuesObject;
    }

    public void loadConfig(JsonObject object) {
        for (Module module : ModuleManager.getAllModules()) {
            if (object.has(module.name)) {
                JsonObject moduleObject = object.get(module.name).getAsJsonObject();
                if (moduleObject.has("enable")) {
                    module.enable = moduleObject.get("enable").getAsBoolean();
                }
                if (moduleObject.has("key")) {
                    module.keyCode = moduleObject.get("key").getAsInt();
                }
                if (!moduleObject.has("values")) {
                    continue;
                }
                JsonObject valuesObject = moduleObject.get("values").getAsJsonObject();
                for (Value<?> value : module.values) {
                    if (valuesObject.has(value.name)) {
                        JsonElement theValue = valuesObject.get(value.name);
                        if (value instanceof NumberValue) {
                            ((NumberValue)value).setValue(theValue.getAsNumber().doubleValue());
                        }
                        else if (value instanceof BooleanValue) {
                            ((BooleanValue)value).setValue(theValue.getAsBoolean());
                        }
                        else if (value instanceof ModeValue) {
                            ((ModeValue)value).setValue(theValue.getAsString());
                        }
                        else if (value instanceof ColorValue) {
                            ((ColorValue)value).setValue(theValue.getAsInt());
                        }
                    }
                }
            }
        }
    }

}
