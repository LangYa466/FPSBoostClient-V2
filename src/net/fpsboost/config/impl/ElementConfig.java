package net.fpsboost.config.impl;

import com.google.gson.JsonObject;
import net.fpsboost.config.Config;
import net.fpsboost.element.Element;
import net.fpsboost.element.ElementManager;

import java.util.stream.Collectors;

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
            hudObject.addProperty("x", hud.xPos);
            hudObject.addProperty("y", hud.yPos);
            object.add(hud.name, hudObject);
        });
        return object;
    }

    @Override
    public void loadConfig(final JsonObject object) {
        ElementManager.elements.forEach(hud -> {
            JsonObject hudObject = object.getAsJsonObject(hud.name);
            if (hudObject != null) {
                hud.xPos = hudObject.get("x").getAsInt();
                hud.yPos = hudObject.get("y").getAsInt();
            }
        });
    }
}
