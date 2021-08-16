package me.GK.core.commands.subcommands;

import me.GK.core.GKCore;
import me.GK.core.cloudnet.CloudNetUtils;
import me.GK.core.modules.Commands.CommandManager;
import me.GK.core.modules.Commands.subcommands.Base;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SendTask extends Base {
    public SendTask(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if(args.length > 2){
            Player target = Bukkit.getPlayer(args[1]);
            String taskName = args[2];
            CloudNetUtils.sendTask(target, taskName);
            GKCore.instance.messageSystem.send(sender, "commands.done");
        }
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.sendTask");
    }

    @Override
    public boolean canExecute(CommandSender sender, String[] args) {
        return sender.hasPermission("gkcore.command.sendTask");
    }
}
