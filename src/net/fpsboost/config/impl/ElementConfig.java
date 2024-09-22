package net.fpsboost.config.impl;

import com.google.gson.JsonObject;
import net.fpsboost.Wrapper;
import net.fpsboost.config.Config;
import net.fpsboost.element.Element;
import net.fpsboost.element.ElementManager;

/**
 * @author LangYa
 * @since 2024/9/3 18:21
 */
public class ElementConfig extends Config implements Wrapper {

    public ElementConfig() {
        super("Element");
    }

    public JsonObject saveConfig() {
        final JsonObject object = new JsonObject();
        for (Element hud : ElementManager.elements) {
            final JsonObject hudObject = new JsonObject();
            hudObject.addProperty("x", hud.xPos);
            hudObject.addProperty("y", hud.yPos);
            hudObject.addProperty("enable", hud.enable);
            object.add(hud.name, hudObject);
        }
        return object;
    }

    public void loadConfig(final JsonObject object) {
        for (Element hud : ElementManager.elements) {
            if (object.has(hud.name)) {
                final JsonObject hudObject = object.get(hud.name).getAsJsonObject();
                hud.xPos = hudObject.get("x").getAsInt();
                hud.yPos = hudObject.get("y").getAsInt();
                hud.enable = hudObject.get("enable").getAsBoolean();
            }
        }
    }
}
