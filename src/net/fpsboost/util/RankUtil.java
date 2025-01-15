package net.fpsboost.util;

import cn.langya.Logger;
import net.fpsboost.Client;
import net.fpsboost.util.network.WebUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
            // 异步获取Web请求数据并处理
            String web = WebUtil.get(Client.web + "rank.txt");

            if (web == null) {
                Logger.error("Failed to get rank data from server.");
                return;
            }

            // 处理排名数据
            ranks = parseStrings(web);
            Logger.debug(ranks.toString());
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
        return ranks.getOrDefault(ign, "Unknown");
    }

    /**
     * 使用换行符（\n）分割输入字符串，并返回Map
     *
     * @param input 输入字符串
     * @return 解析后的排名数据Map
     */
    public static Map<String, String> parseStrings(String input) {
        return Arrays.stream(input.split("\n"))  // 使用 split("\n") 分割输入字符串
                .map(line -> line.split("-"))
                .filter(parts -> parts.length == 2)  // 过滤无效数据
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }
}
