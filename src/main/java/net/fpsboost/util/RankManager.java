package net.fpsboost.util;

import net.fpsboost.Wrapper;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author LangYa
 * @updateSince 2025/01/26
 * @since 2024/6/5 下午9:25
 */
public class RankManager implements Wrapper {
    public static final String PRIMARY_COLOR = EnumChatFormatting.RED.toString();
    public static final String SECONDARY_COLOR = EnumChatFormatting.GRAY.toString();

    public static void hook(DrawTextHook e) {
        if (Wrapper.isNull()) return;
        RankUtil.ranks.forEach((playerName, rank) -> set(e, playerName, rank));
    }

    private static String getRank(String str, String color) {
        return SECONDARY_COLOR + "(" + color + EnumChatFormatting.BOLD + str + SECONDARY_COLOR + ")" + EnumChatFormatting.UNDERLINE;
    }

    private static String replaceAllOccurrences(String text, String target, String replacement) {
        // sb none text 例如kk craft就会这样
        if (target == null || target.isEmpty() || replacement == null) {
            return text;
        }

        StringBuilder sb = new StringBuilder(text);
        int index = sb.indexOf(target);

        while (index != -1) {
            sb.replace(index, index + target.length(), replacement);
            index += replacement.length();  // Move past the replacement to avoid infinite loop
            index = sb.indexOf(target, index);
        }

        return sb.toString();
    }

    private static void set(DrawTextHook e, String playerName, String rank) {
        boolean set = false;
        if (e.getDisplayText().contains(playerName) && !set) {
            String rankPrefix;
            if (rank.equals("Admin")) {
                rankPrefix = getRank(rank, PRIMARY_COLOR);
            } else {
                rankPrefix = getRank(rank, EnumChatFormatting.BLUE.toString());
            }

            // e.text.replaceAll(playerName,rankPrefix + playerName);
            e.setDisplayText(replaceAllOccurrences(e.getDisplayText(), playerName, playerName + rankPrefix));

            /*
            int playerNameIndex = e.text.indexOf(playerName);

            if (playerNameIndex != -1) {

                // 构建新的字符串
                e.text = e.text.substring(0, playerNameIndex) + rankPrefix + e.text.substring(playerNameIndex);
            }
             */
            set = true;

        }
    }

}