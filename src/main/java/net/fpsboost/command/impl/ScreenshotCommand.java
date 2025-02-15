package net.fpsboost.command.impl;

import net.fpsboost.Wrapper;
import net.fpsboost.command.Command;
import net.fpsboost.util.Logger;
import net.fpsboost.util.screenShot.FileTransferable;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author LangYa466
 * @since 2/15/2025
 */
public class ScreenshotCommand extends Command implements Wrapper {
    public ScreenshotCommand() {
        super("screenshot", "error");
    }

    @Override
    public void run(String[] args) {
        if (args.length < 3) {
            mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§c用法: .screenshot <open/copy/del> <文件名>"));
            return;
        }

        File file = new File("screenshots", args[2]);

        switch (args[1].toLowerCase()) {
            case "open":
                if (!file.exists()) {
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§c文件不存在: " + args[2]));
                    return;
                }
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    Logger.error(e);
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§c无法打开文件: " + args[2]));
                }
                break;

            case "copy":
                if (!file.exists()) {
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§c文件不存在: " + args[2]));
                    return;
                }
                FileTransferable selection = new FileTransferable(file);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§a已复制到剪贴板: " + args[2]));
                break;

            case "del":
                if (!file.exists()) {
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§c文件不存在: " + args[2]));
                    return;
                }
                if (file.delete()) {
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§a删除成功: " + args[2]));
                } else {
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§c删除失败: " + args[2]));
                }
                break;

            default:
                mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§c未知命令: " + Arrays.toString(args)));
                break;
        }
    }
}
