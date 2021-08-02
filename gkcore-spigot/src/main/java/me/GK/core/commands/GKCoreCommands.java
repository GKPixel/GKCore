package me.GK.core.commands;

import me.GK.core.commands.subcommands.Item;
import me.GK.core.commands.subcommands.Language;
import me.GK.core.commands.subcommands.Placeholder;
import me.GK.core.commands.subcommands.ReconnectSQL;
import me.GK.core.commands.subcommands.Reload;
import me.GK.core.modules.Commands.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GKCoreCommands {
    public static void register(JavaPlugin plugin) {
        CommandManager cmd = new CommandManager(plugin, "gk");
        cmd.commands.add(new Reload(plugin, cmd, "reload", false));
        cmd.commands.add(new ReconnectSQL(plugin, cmd, "reconnect_sql", false));
        cmd.commands.add(new Placeholder(plugin, cmd, "placeholder", false));
        cmd.commands.add(new Item(plugin, cmd, "item", false));
        cmd.commands.add(new Language(plugin, cmd, "language", false));
    }
}