package com.gkpixel.core.modules.Commands.subcommands;

import com.gkpixel.core.modules.Commands.CommandManager;
import com.gkpixel.core.modules.Commands.InvalidArgumentException;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public abstract class Base {
    protected final JavaPlugin plugin;
    protected final CommandManager cmd;

    private final String name;
    private final boolean hidden;

    public Base(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        this.plugin = plugin;
        this.cmd = cmd;

        this.name = name;
        this.hidden = hidden;
    }

    public final String getName() {
        return name;
    }

    public final boolean isHidden() {
        return hidden;
    }

    public void recordUsage(Map<Base, Integer> commandUsage) {
        commandUsage.merge(this, 1, Integer::sum);
    }

    /**
     * A function that can be overridden when there needs to be a check
     * Do note that this should only be used with short checks like permissions checks,
     * time-consuming checks should be done within the command execution
     * since this check is also ran when tab complete happens and decides whether the server should send a tab hint
     *
     * @param sender The command sender
     * @param args   Command arguments
     * @return Whether the command sender can execute this subcommand
     */
    public boolean canExecute(CommandSender sender, String[] args) {
        return true;
    }

    /**
     * The main function that handles command execution
     * Do note that args normally starts with 1 (0 is the subcommand's name)
     *
     * @param sender The command sender
     * @param args   Command arguments
     */
    public abstract void onExecute(CommandSender sender, String[] args) throws InvalidArgumentException;

    /**
     * A function that returns the description of the command
     *
     * @param sender The command sender
     * @return A String containing the description of the commandd
     */
    public abstract String getDescription(CommandSender sender);

    /**
     * A function that can be overridden when the plugin needs a tab completer for its arguments
     * Do note when you are trying auto complete resource heavy stuff, you should implement a canExecute to check
     * whether the sender has the ability to execute this command, to prevent excessive resource consumption
     * You can utilize CommandManager.createReturnList to return the best results with a single line
     *
     * @param sender The command sender
     * @param args   Command arguments
     * @return A list of strings that contains the available choices to an argument
     */
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }
}
