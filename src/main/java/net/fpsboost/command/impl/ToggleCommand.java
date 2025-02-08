package net.fpsboost.command.impl;

import net.fpsboost.command.Command;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.ChatUtil;

import java.util.Optional;

/**
 * @author LangYa466
 * @since 2025/1/11
 */
public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("t", ".t 模块名称");
    }

    @Override
    public void run(String[] args) {
        if (args.length != 2) {
            ChatUtil.addMessageWithClient(usage);
            return;
        }

        String moduleName = args[1];

        Optional<Module> moduleOpt = ModuleManager.modules.stream()
                .filter(module -> module.name.equals(moduleName) || module.cnName.equals(moduleName))
                .findFirst();

        if (moduleOpt.isPresent()) {
           moduleOpt.get().toggle();
        } else {
            ChatUtil.addMessageWithClient("找不到有这个名字的模块");
        }
    }
}
