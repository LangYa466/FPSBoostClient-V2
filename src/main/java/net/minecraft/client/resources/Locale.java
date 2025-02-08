package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Locale {
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    final Map<String, String> properties = new ConcurrentHashMap<>();
    @Getter
    private boolean unicode;

    public synchronized void loadLocaleDataFiles(IResourceManager resourceManager, List<String> languageList) {
        this.properties.clear();

        for (String language : languageList) {
            String langFile = String.format("lang/%s.lang", language);

            for (String domain : resourceManager.getResourceDomains()) {
                try {
                    List<IResource> resources = resourceManager.getAllResources(new ResourceLocation(domain, langFile));
                    this.loadLocaleData(resources);
                } catch (IOException ignored) {
                    // Log the error if necessary
                }
            }
        }

        this.checkUnicode();
    }

    private void checkUnicode() {
        int unicodeCount = 0;
        int totalCount = 0;

        for (String value : this.properties.values()) {
            int length = value.length();
            totalCount += length;

            for (int i = 0; i < length; i++) {
                if (value.charAt(i) >= 256) {
                    unicodeCount++;
                }
            }
        }

        this.unicode = (double) unicodeCount / totalCount > 0.1;
    }

    private void loadLocaleData(List<IResource> resources) throws IOException {
        for (IResource resource : resources) {
            try (InputStream inputStream = resource.getInputStream()) {
                this.loadLocaleData(inputStream);
            }
        }
    }

    private void loadLocaleData(InputStream inputStream) throws IOException {
        for (String line : IOUtils.readLines(inputStream, Charsets.UTF_8)) {
            if (!line.isEmpty() && line.charAt(0) != '#') {
                String[] keyValue = Iterables.toArray(splitter.split(line), String.class);

                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = pattern.matcher(keyValue[1]).replaceAll("%$1s");
                    this.properties.put(key, value);
                }
            }
        }
    }

    private String translateKeyPrivate(String key) {
        return this.properties.getOrDefault(key, key);
    }

    public String formatMessage(String key, Object[] parameters) {
        String message = this.translateKeyPrivate(key);

        try {
            return String.format(message, parameters);
        } catch (IllegalFormatException e) {
            return "Format error: " + message;
        }
    }
}
