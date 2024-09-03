package net.fpsboost.command;

import net.fpsboost.command.impl.*;

import java.util.ArrayList;

/**
 * @author LangYa
 * @since 2024/9/1 18:50
 */
public class CommandManager {
    public static final ArrayList<Command> commands = new ArrayList<>();

    public static void init() {
        commands.add(new BindCommand());
    }

    public static boolean runCommand(String message) {
        String[] args = message.split(" ");
        String commandName = args[0].replace(".","");
        for (Command command : commands) {
            if (command.name.equals(commandName)) {
                command.run(args);
                return true;
            }
        }

        return false;
    }

}
