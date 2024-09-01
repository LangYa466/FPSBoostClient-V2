package net.fpsboost.command;

/**
 * @author LangYa
 * @since 2024/9/1 18:49
 */
public class Command {
    public String name;
    public String usage;

    public Command(String name, String usage) {
        this.name = name;
        this.usage = usage;
    }

    public void run(String[] args) { }

}
