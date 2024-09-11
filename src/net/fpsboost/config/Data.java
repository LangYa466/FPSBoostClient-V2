package net.fpsboost.config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LangYa
 * @since 2024/9/3 19:18
 */
public class Data {
    private final HashMap<String, String> data;

    public Data() {
        data = new HashMap<>();
    }

    public void parse(String input) {
        String[] pairs = input.split("\n");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                data.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
    }

    public String put(String key,String value) {
        return data.put(key,value);
    }

    public String get(String key) {
        String dg = data.get(key);
        if (dg != null) return dg;
        return "0";
    }

    public void saveToFile(String file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
        }
    }

    public void loadFromFile(String file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            parse(content.toString());
        }
    }
}
