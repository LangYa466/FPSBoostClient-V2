package net.fpsboost.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fpsboost.config.Config;
import net.fpsboost.element.ElementManager;

/**
 * @author LangYa
 * @since 2024/9/3 18:21
 */
public class ElementConfig extends Config {

    public ElementConfig() {
        super("Element");
    }

    @Override
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();
        ElementManager.elements.forEach(hud -> {
            JsonObject hudObject = new JsonObject();
            hudObject.addProperty("posX", hud.xPos);
            hudObject.addProperty("posY", hud.yPos);
            hudObject.addProperty("scale", hud.scale);
            hudObject.addProperty("enable", hud.enable);
            object.add(hud.name, hudObject);
        });
        return object;
    }

    @Override
    public void loadConfig(final JsonObject object) {
        ElementManager.elements.forEach(hud -> {
            JsonObject hudObject = object.getAsJsonObject(hud.name);
            if (hudObject != null) {
                hud.xPos = hudObject.get("posX").getAsInt();
                hud.yPos = hudObject.get("posY").getAsInt();
                hud.scale = hudObject.get("scale").getAsInt();
                JsonElement enableElement = hudObject.get("enable");
                boolean asBoolean = enableElement != null && enableElement.isJsonPrimitive() && enableElement.getAsBoolean();

                hud.setEnable(asBoolean, true);
            }
        });
    }
}
