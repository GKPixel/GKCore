package com.gkpixel.core.commands;

import com.gkpixel.core.commands.subcommands.ApplyTemplate;
import com.gkpixel.core.commands.subcommands.CloudNet;
import com.gkpixel.core.commands.subcommands.Item;
import com.gkpixel.core.commands.subcommands.Language;
import com.gkpixel.core.commands.subcommands.LaunchToLocation;
import com.gkpixel.core.commands.subcommands.Placeholder;
import com.gkpixel.core.commands.subcommands.ReconnectSQL;
import com.gkpixel.core.commands.subcommands.Reload;
import com.gkpixel.core.commands.subcommands.RunCommandWithInput;
import com.gkpixel.core.commands.subcommands.RunOfflineCommand;
import com.gkpixel.core.commands.subcommands.SendTask;
import com.gkpixel.core.modules.Commands.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GKCoreCommands {
    public static void register(JavaPlugin plugin) {
        CommandManager cmd = new CommandManager(plugin, "gk");
        cmd.commands.add(new Reload(plugin, cmd, "reload", false));
        cmd.commands.add(new ReconnectSQL(plugin, cmd, "reconnect_sql", false));
        cmd.commands.add(new Placeholder(plugin, cmd, "placeholder", false));
        cmd.commands.add(new Item(plugin, cmd, "item", false));
        cmd.commands.add(new Language(plugin, cmd, "language", false));
        cmd.commands.add(new SendTask(plugin, cmd, "sendtask", false));
        cmd.commands.add(new ApplyTemplate(plugin, cmd, "applytemplate", false));
        cmd.commands.add(new CloudNet(plugin, cmd, "cloudnet", false));
        cmd.commands.add(new RunOfflineCommand(plugin, cmd, "runofflinecommand", false));
        cmd.commands.add(new RunCommandWithInput(plugin, cmd, "runcommandwithinput", false));
        cmd.commands.add(new LaunchToLocation(plugin, cmd, "launchtolocation", false));
    }
}
