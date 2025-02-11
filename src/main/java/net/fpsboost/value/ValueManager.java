package net.fpsboost.value;

import net.fpsboost.element.ElementManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.Logger;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LangYa
 * @since 2024/9/1 20:08
 */
public class ValueManager {

    private static final Unsafe UNSAFE = getUnsafe();
    private static final Map<Class<?>, List<FieldOffset>> valueFieldCache = new HashMap<>();

    public static void init() {
        ModuleManager.modules.forEach(ValueManager::addValues);
        ElementManager.elements.forEach(ValueManager::addValues);
    }

    private static void addValues(Module module) {
        // 获取或缓存字段偏移量
        List<FieldOffset> fields = valueFieldCache.computeIfAbsent(module.getClass(), ValueManager::findValueFields);

        List<Value<?>> valueList = new ArrayList<>(fields.size());
        for (FieldOffset field : fields) {
            try {
                Value<?> value = (Value<?>) UNSAFE.getObject(module, field.offset);
                if (value != null) valueList.add(value);
            } catch (Exception e) {
                Logger.error("Failed to register value for module [{}]: {}", module.name, e.getMessage());
            }
        }
        module.values.addAll(valueList);
    }

    private static List<FieldOffset> findValueFields(Class<?> clazz) {
        List<FieldOffset> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Value.class.isAssignableFrom(field.getType())) {
                try {
                    long offset = UNSAFE.objectFieldOffset(field);
                    fields.add(new FieldOffset(field, offset));
                } catch (Exception ignored) {
                }
            }
        }
        return fields;
    }

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Unsafe instance", e);
        }
    }

    private static class FieldOffset {
        final Field field;
        final long offset;

        FieldOffset(Field field, long offset) {
            this.field = field;
            this.offset = offset;
        }
    }
}
