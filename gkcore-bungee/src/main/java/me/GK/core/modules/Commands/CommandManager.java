package me.GK.core.modules.Commands;

import com.google.common.collect.ImmutableList;
import me.GK.core.main.Extensions;
import me.GK.core.modules.Commands.subcommands.Base;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager extends Command implements TabExecutor {
    private static final int MAX_SUGGESTIONS = 80;
    private final Plugin plugin;
    private final Map<Base, Integer> commandUsage = new HashMap<>();
    private final String baseCommand;
    public List<Base> commands = new LinkedList<>();

    public CommandManager(Plugin plugin, String baseCommand, String... aliases) {
        super(baseCommand, null, aliases);
        this.baseCommand = baseCommand;
        this.plugin = plugin;
        this.plugin.getProxy().getPluginManager().registerCommand(plugin, this);
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

    public Plugin getPlugin() {
        return plugin;
    }

    public Map<Base, Integer> getCommandUsage() {
        return commandUsage;
    }

    public void sendHelp(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(""));
        sender.sendMessage(TextComponent.fromLegacyText(Extensions.color("&a" + plugin.getDescription().getName() + " &2v" + getPlugin().getDescription().getVersion())));
        sender.sendMessage(TextComponent.fromLegacyText(""));

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
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            for (Base command : commands) {
                if (args[0].equalsIgnoreCase(command.getName()) && command.canExecute(sender, args)) {
                    command.recordUsage(commandUsage);
                    command.onExecute(sender, args);
                }
            }
        } else {
            sendHelp(sender, args);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
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
                if (command.getName().equals(args[1]) && command.canExecute(sender, args))
                    return command.onTabComplete(sender, args);
            }
            return ImmutableList.of();
        }
    }
}