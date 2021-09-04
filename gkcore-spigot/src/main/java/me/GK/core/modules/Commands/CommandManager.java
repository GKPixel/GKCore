package me.GK.core.modules.Commands;

import com.google.common.collect.ImmutableList;
import me.GK.core.GKCore;
import me.GK.core.main.Extensions;
import me.GK.core.modules.Commands.subcommands.Base;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {
    private static final int MAX_SUGGESTIONS = 80;
    private final JavaPlugin plugin;
    private final Map<Base, Integer> commandUsage = new HashMap<>();
    private final String baseCommand;
    public List<Base> commands = new LinkedList<>();

    public CommandManager(JavaPlugin plugin, String baseCommand, String... aliases) {
        this.plugin = plugin;
        this.baseCommand = baseCommand;
        PluginCommand cmd = this.plugin.getCommand(baseCommand);
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
        if (aliases.length != 0) {
            for (String alias : aliases) {
                PluginCommand als = this.plugin.getCommand(alias);
                als.setExecutor(this);
                als.setTabCompleter(this);
            }
        }
    }

    /**
     * Generates a list of possible auto complete guesses based on given arguments, mainly used for tab completing
     *
     * @param list   The list of possible choices
     * @param string The string of whatever the user has already typed
     * @param strict Whether to check via startsWith (strict) or contains (non-strict)
     * @return A List of String s containing all possible guesses
     */
    public static List<String> createReturnList(List<String> list, String string, boolean strict) {
        if (string.length() == 0) {
            return list;
        }

        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new LinkedList<>();

        for (String item : list) {
            if (returnList.size() >= MAX_SUGGESTIONS) {
                break;
            }
            if (strict && item.toLowerCase(Locale.ROOT).startsWith(input)) {
                returnList.add(item);
            } else if ((!strict) && item.toLowerCase(Locale.ROOT).contains(input)) {
                returnList.add(item);
            } else if (item.equalsIgnoreCase(input)) {
                return Collections.emptyList();
            }
        }

        return returnList;
    }

    public static String getArgument(String[] args, int index, CommandSender sender) throws InvalidArgumentException {
        if (args.length < (index + 1)) {
            if (sender != null) {
                GKCore.instance.messageSystem.send(sender, "commands.missingArgument");
            }
            throw new InvalidArgumentException();
        } else {
            return args[index];
        }
    }

    public static Player getArgumentPlayer(String[] args, int index, CommandSender sender) throws InvalidArgumentException {
        String result = getArgument(args, index, sender);
        if (result != null) {
            if (Bukkit.getPlayer(result) == null) {
                GKCore.instance.messageSystem.send(sender, "commands.invalidPlayer");
            } else {
                return Bukkit.getPlayer(result);
            }
        }
        throw new InvalidArgumentException();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Map<Base, Integer> getCommandUsage() {
        return commandUsage;
    }

    public void sendHelp(CommandSender sender, String[] args) {
        sender.sendMessage("");
        sender.sendMessage(Extensions.color("&a" + plugin.getName() + " &2v" + getPlugin().getDescription().getVersion()));
        sender.sendMessage("");

        for (Base cmd : commands) {
            if (!cmd.isHidden() && cmd.canExecute(sender, args)) {
                sender.sendMessage(Extensions.color("&7/" + baseCommand + " " + cmd.getName() + " &b") + cmd.getDescription(sender));
            }
        }
    }

    public List<String> getSubCommandNames() {
        return commands.stream().map(Base::getName).collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (Base command : commands) {
                if (args[0].equalsIgnoreCase(command.getName())) {
                    if (command.canExecute(sender, args)) {
                        command.recordUsage(commandUsage);
                        try {
                            command.onExecute(sender, args);
                        } catch (InvalidArgumentException ignored) {

                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            sendHelp(sender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> subcommandNames = new LinkedList<>();
            for (Base subcommand : commands) {
                if (subcommand.canExecute(sender, args)) {
                    subcommandNames.add(subcommand.getName());
                }
            }
            return createReturnList(subcommandNames, args[0], false);
        } else {
            for (Base command : commands) {
                if (command.getName().equals(args[0]) && command.canExecute(sender, args))
                    return command.onTabComplete(sender, args);
            }
            return ImmutableList.of();
        }
    }
}