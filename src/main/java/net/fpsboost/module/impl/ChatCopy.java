package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * @author LangYa466
 * @since 2/4/2025
 */
public class ChatCopy extends Module {
    public ChatCopy() {
        super("ChatCopy", "聊天框消息复制");
    }

    public static boolean isEnable;

    @Override
    public void onEnable() {
        isEnable = true;
    }

    @Override
    public void onDisable() {
        isEnable = false;
    }

    public static void onChat(IChatComponent chatComponent) {
        if (!isEnable) return;
        chatComponent.appendSibling(
                new ChatComponentText(EnumChatFormatting.GRAY + " [C]").setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                new StringBuilder().insert(0, "/messagecopy ").append(EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getFormattedText())).toString()))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("按我即可复制信息")))));
    }

    public static boolean onSendMessage(String message) {
        if (!isEnable) return false;
        if (!message.startsWith("/")) return false;
        String s = message.substring(1);
        final String[] command = s.split(" ");
        if (command.length > 0) {
            StringBuilder msg = new StringBuilder();
            for (int index = 1; index < command.length; ++index)
                msg.append(command[index]).append(" ");
            if (command[0].equals("messagecopy")) {
                StringSelection stsel = new StringSelection(msg.toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
                return true;
            }
        }
        return false;
    }
}
