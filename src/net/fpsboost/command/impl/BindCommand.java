package net.fpsboost.command.impl;

import net.fpsboost.command.Command;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.ChatUtil;
import org.lwjgl.input.Keyboard;

/**
 * @author LangYa
 * @since 2024/9/1 18:50
 */
public class BindCommand extends Command {
    public BindCommand() {
        super("bind",".bind 模块名称 按键名称");
    }

    @Override
    public void run(String[] args) {
        if (args.length == 3) {
            for (Module module : ModuleManager.modules) {
                String moduleName = args[1];
                if (module.name.equals(moduleName) || module.cnName.equals(moduleName)) {
                    String keyName = args[2];
                    int bindKeyCode = Keyboard.getKeyIndex(keyName.toUpperCase());
                    if (bindKeyCode != 0) {
                        ChatUtil.addMessageWithClient("绑定按键成功");
                        module.keyCode = bindKeyCode;
                    } else {
                        ChatUtil.addMessageWithClient("找不到有这个名字的按键");
                    }
                } else {
                    ChatUtil.addMessageWithClient("找不到有这个名字的模块");
                }
            }
        } else {
            ChatUtil.addMessageWithClient(usage);
        }
    }
}
