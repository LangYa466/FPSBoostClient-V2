package net.fpsboost.command;

import net.fpsboost.command.impl.BindCommand;
import net.fpsboost.command.impl.ScreenshotCommand;
import net.fpsboost.command.impl.ToggleCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author LangYa
 * @since 2024/9/1 18:50
 */
public class CommandManager {
    public static final ArrayList<Command> commands = new ArrayList<>();

    public static void init() {
        addCommands(new BindCommand(), new ToggleCommand(), new ScreenshotCommand());
    }

    private static void addCommands(Command... commands) {
        CommandManager.commands.addAll(Arrays.asList(commands));
    }

    public static boolean runCommand(String message) {
        String[] args = message.split(" ");
        String commandName = args[0].replace(".", "");

        for (Command command : commands) {
            if (command.name.equals(commandName)) {
                command.run(args);
                return true;
            }
        }
        return false;
    }
}
