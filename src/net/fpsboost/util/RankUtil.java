package net.fpsboost.util;

import cn.langya.Logger;
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

    public static Map<String, String> ranks = new HashMap<>();

    /**
     * 异步获取排名数据
     */
    public static void getRanksAsync() {
        CompletableFuture.runAsync(() -> {
            // 在异步线程中执行Web请求和数据处理
            String web = WebUtil.get(Client.web + "rank.txt");
            String[] strings;
            if (web != null) {
                strings = parseStrings(web);
            } else {
                Logger.error("Failed to get rank data from server.");
                return;
            }
            ranks = parseString(strings);
        }).exceptionally(ex -> {
            Logger.error(ex.getMessage());
            return null;
        });
    }

    /**
     * 获取排名，注意这里是同步调用，会阻塞直到获取到结果
     *
     * @param ign 玩家名称
     * @return 玩家排名
     */
    public static String getRank(String ign) {
        return ranks.get(ign);
    }

    // 使用换行符（\n）分割输入字符串
    public static String[] parseStrings(String input) {
        return input.split("\n");
    }

    public static Map<String, String> parseString(String[] inputs) {
        Map<String, String> result = new HashMap<>();

        for (String input : inputs) {
            // 使用 "-" 进行分割
            String[] parts = input.split("-");

            if (parts.length == 2) {
                // 将解析出的字符串存入Map
                result.put(parts[0], parts[1]);
            }
        }
        return result;
    }
}
