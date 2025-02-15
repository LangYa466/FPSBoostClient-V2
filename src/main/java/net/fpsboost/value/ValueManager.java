package net.fpsboost.value;

import net.fpsboost.element.ElementManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.Logger;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author LangYa
 * @since 2024/9/1 20:08
 */
public class ValueManager {
    private static final Map<Class<?>, List<Field>> valueFieldCache = new HashMap<>();

    public static void init() {
        ModuleManager.modules.forEach(ValueManager::addValues);
        ElementManager.elements.forEach(ValueManager::addValues);
    }

    private static void addValues(Module module) {
        List<Field> fields = valueFieldCache.computeIfAbsent(module.getClass(), ValueManager::findValueFields);
        List<Value<?>> valueList = new ArrayList<>(fields.size());

        for (Field field : fields) {
            try {
                field.setAccessible(true); // 确保可以访问 private 字段
                Object value = field.get(module);
                if (value instanceof Value<?>) {
                    valueList.add((Value<?>) value);
                }
            } catch (IllegalAccessException e) {
                Logger.error("Failed to register value for module [{}]: {}", module.name, e.getMessage());
            }
        }
        module.values.addAll(valueList);
    }

    private static List<Field> findValueFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Value.class.isAssignableFrom(field.getType())) {
                fields.add(field);
            }
        }
        return fields;
    }
}