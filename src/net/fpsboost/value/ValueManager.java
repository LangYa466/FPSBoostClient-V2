package net.fpsboost.value;

import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;

import java.lang.reflect.Field;

/**
 * @author LangYa
 * @since 2024/9/1 20:08
 */
public class ValueManager {

    public static void init() {
        for (Module module : ModuleManager.modules) {
            for (Field field : module.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    final Object obj = field.get(module);
                    if (obj instanceof Value) module.values.add((Value) obj);
                } catch (IllegalAccessException e) {
                    System.out.printf("%s register value error : %s%n",module.name,e.getMessage());
                }
            }
        }
    }

}
