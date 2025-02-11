package net.fpsboost.util;

import net.fpsboost.Client;
import net.fpsboost.util.network.WebUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author LangYa466
 * @since 2025/1/4
 */
public class RankUtil {

    public static final Map<String, String> ranks = new HashMap<>();
    private static volatile boolean hasError = false; // 使用 volatile 以保证线程可见性

    /**
     * 异步获取排名数据
     */
    public static void getRanksAsync() {
        if (hasError) return;

        CompletableFuture.runAsync(() -> {
            String web = WebUtil.get(Client.web + "rank.txt");
            if (web == null || web.isEmpty()) {
                Logger.error("Failed to get rank data from server.");
                hasError = true;
                return;
            }

            Map<String, String> parsedRanks = parseStrings(web);
            if (!parsedRanks.isEmpty()) {
                synchronized (ranks) { // 避免多个线程同时修改
                    ranks.clear();
                    ranks.putAll(parsedRanks);
                }
                Logger.debug("Updated rank data: " + ranks.size() + " entries.");
            } else {
                Logger.warn("Parsed rank data is empty.");
            }
        });
    }

    /**
     * 获取排名（同步调用）
     *
     * @param ign 玩家名称
     * @return 玩家排名
     */
    public static String getRank(String ign) {
        synchronized (ranks) { // 保证线程安全
            return ranks.getOrDefault(ign, "Unknown");
        }
    }

    /**
     * 解析排名数据
     *
     * @param input 输入字符串
     * @return 解析后的排名数据Map
     */
    private static Map<String, String> parseStrings(String input) {
        Map<String, String> parsedRanks = new HashMap<>();
        for (String line : input.split("\n")) {
            int index = line.indexOf('-');
            if (index > 0 && index < line.length() - 1) {
                parsedRanks.put(line.substring(0, index), line.substring(index + 1));
            }
        }
        return parsedRanks;
    }
}
