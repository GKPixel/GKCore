package me.GK.core.commands.subcommands;

import me.GK.core.GKCore;
import me.GK.core.modules.Commands.CommandManager;
import me.GK.core.modules.Commands.subcommands.Base;
import me.GK.core.mysql.MySQL;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReconnectSQL extends Base {
    public ReconnectSQL(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (MySQL.isConnected())
            MySQL.reconnect();
        else
            MySQL.connect();
        GKCore.instance.messageSystem.send(sender, "commands.done");
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.reconnectSQLDescription");
    }

    @Override
    public boolean canExecute(CommandSender sender, String[] args) {
        return sender.hasPermission("gkcore.command.reconnectsql");
    }
}
