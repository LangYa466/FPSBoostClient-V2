package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ResourceIndex {
    private static final Logger logger = LogManager.getLogger();
    private final Map<String, File> resourceMap = new ConcurrentHashMap<>();

    public ResourceIndex(File baseDir, String indexName) {
        if (indexName != null) {
            File objectsDir = new File(baseDir, "objects");
            File indexFile = new File(baseDir, "indexes/" + indexName + ".json");

            try (BufferedReader reader = Files.newReader(indexFile, Charsets.UTF_8)) {
                JsonObject rootJson = (new JsonParser()).parse(reader).getAsJsonObject();
                JsonObject objectsJson = JsonUtils.getJsonObject(rootJson, "objects", null);

                if (objectsJson != null) {
                    for (Map.Entry<String, JsonElement> entry : objectsJson.entrySet()) {
                        processResourceEntry(entry, objectsDir);
                    }
                }
            } catch (JsonParseException e) {
                logger.error("Failed to parse resource index file: {}", indexFile, e);
            } catch (FileNotFoundException e) {
                logger.error("Resource index file not found: {}", indexFile, e);
            } catch (Exception e) {
                logger.error("Unexpected error while loading resource index file: {}", indexFile, e);
            }
        }
    }

    private void processResourceEntry(Map.Entry<String, JsonElement> entry, File objectsDir) {
        try {
            JsonObject entryValue = entry.getValue().getAsJsonObject();
            String originalKey = entry.getKey();
            String resourceKey = originalKey.replace("/", ":");
            String hash = JsonUtils.getString(entryValue, "hash");
            File resourceFile = new File(objectsDir, hash.substring(0, 2) + "/" + hash);
            resourceMap.put(resourceKey, resourceFile);
        } catch (Exception e) {
            logger.warn("Error processing resource entry: {}", entry.getKey(), e);
        }
    }

}
