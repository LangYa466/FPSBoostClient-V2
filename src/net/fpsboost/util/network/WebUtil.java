package net.fpsboost.util.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebUtil {
    
    public static String get(String url) {
        try {
            HttpURLConnection connection = getHttpURLConnection(url);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Specify the character encoding
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line).append("\n");
                }
                reader.close();
                return responseBuilder.toString();
            }
            throw new IOException("HTTP request failed with response code: " + responseCode);
        }
        catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    private static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");

        // Set browser-like headers
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        return connection;
    }
}
