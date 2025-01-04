package net.fpsboost.value;

import cn.langya.Logger;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;

import java.lang.reflect.Field;

/**
 * @author LangYa
 * @since 2024/9/1 20:08
 */
public class ValueManager {

    public static void init() {
        ModuleManager.modules.forEach(ValueManager::addValues);
        ElementManager.elements.forEach(ValueManager::addValues);
    }

    private static void addValues(Module module) {
        for (Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(module);
                if (obj instanceof Value) module.values.add((Value) obj);
            } catch (IllegalAccessException e) {
                Logger.error("{} register value error : {}",module.name,e.getMessage());
            }
        }
    }

}
