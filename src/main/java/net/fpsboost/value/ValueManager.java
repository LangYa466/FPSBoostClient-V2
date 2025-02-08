package net.fpsboost.value;

import net.fpsboost.util.Logger;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

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
        // 临时列表，避免多次操作 module.values
        List<Value<?>> valueList = new ArrayList<>();
        for (Field field : module.getClass().getDeclaredFields()) {
            if (!Value.class.isAssignableFrom(field.getType())) continue; // 过滤非 Value 类型字段
            try {
                field.setAccessible(true);
                Value<?> value = (Value<?>) field.get(module); // 确保类型安全
                if (value != null) valueList.add(value);
            } catch (IllegalAccessException e) {
                Logger.error("Failed to register value for module [{}]: {}", module.name, e.getMessage());
            }
        }
        // 批量添加值，减少多次操作
        addValuesToModule(module, valueList);
        // 666忘记释放了 原来我在内存堆了屎
        valueList.clear();
    }

    // 通过 Collection<? extends Value<?>> 类型安全地批量添加值
    private static void addValuesToModule(Module module, List<Value<?>> values) {
        module.values.addAll(values); // 添加所有的值
    }
}
