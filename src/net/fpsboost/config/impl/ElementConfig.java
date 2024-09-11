package net.fpsboost.config.impl;

import net.fpsboost.config.Config;
import net.fpsboost.element.Element;
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
    public void save() {
        for (Element element : ElementManager.elements) {
            data.put(element.name + "-Enable", String.valueOf(element.enable));
            data.put(element.name + "-XPos", Integer.toString(element.xPos));
            data.put(element.name + "-YPos", Integer.toString(element.yPos));
        }
    }

    @Override
    public void load() {
        boolean elementNullConfig = false;
        for (Element element : ElementManager.elements) {
            elementNullConfig = element.name.contains(data.toString());
            // 还没写ClickGui 所以enable默认是true
            //            element.enable = Boolean.parseBoolean(data.get(element.name + "-Enable"));
            int xPos = Integer.parseInt(data.get(element.name + "-XPos"));
            if (xPos == 0) xPos = 5;
            element.xPos = xPos;
            int yPos = Integer.parseInt(data.get(element.name + "-YPos"));
            if (yPos == 0) yPos = 5;
            element.yPos = yPos;
        }
        if (elementNullConfig) {
            save();
            load();
        }
    }
}
