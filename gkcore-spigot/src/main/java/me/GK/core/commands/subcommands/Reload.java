package me.GK.core.commands.subcommands;

import me.GK.core.GKCore;
import me.GK.core.modules.Commands.CommandManager;
import me.GK.core.modules.Commands.subcommands.Base;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Reload extends Base {
    public Reload(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        GKCore.instance.configSystem.reload();
        GKCore.instance.updateDebugModeFromConfig();
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.reloadDescription");
    }

    @Override
    public boolean canExecute(CommandSender sender, String[] args) {
        return sender.hasPermission("gkcore.command.reload");
    }
}
