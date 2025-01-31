package net.minecraft.client.resources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class I18n {
    private static Locale i18nLocale;
    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static void setLocale() {
        i18nLocale = LanguageManager.currentLocale;
        cache.clear(); // 切换语言时清空缓存
    }

    public static String format(String translateKey, Object... parameters) {
        String cacheKey = translateKey + "#" + String.join(",", toStringArray(parameters));
        return cache.computeIfAbsent(cacheKey, key -> i18nLocale.formatMessage(translateKey, parameters));
    }

    public static Map<String, String> getLocaleProperties() {
        return i18nLocale.properties;
    }

    private static String[] toStringArray(Object[] parameters) {
        String[] result = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            result[i] = String.valueOf(parameters[i]);
        }
        return result;
    }
}
