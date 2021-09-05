package me.GK.core.commands.subcommands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import me.GK.core.GKCore;
import me.GK.core.modules.Commands.CommandManager;
import me.GK.core.modules.Commands.subcommands.Base;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CloudNet extends Base {
    public CloudNet(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        //joining the command
        String cloudNetCommand = "";
        for (int i = 1; i < args.length; i++) {
            cloudNetCommand += args[i] + " ";
        }

        //send command
        String[] result = CloudNetDriver.getInstance().getNodeInfoProvider().sendCommandLine(cloudNetCommand);

        //success message
        String msg = GKCore.instance.messageSystem.get("cloudNetCommandSent");
        msg = msg.replace("%cmd%", cloudNetCommand);
        sender.sendMessage(msg);

        //console message
        for (String str : result) {
            sender.sendMessage(str);
        }
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.cloudnet");
    }

    @Override
    public boolean canExecute(CommandSender sender, String[] args) {
        return sender.hasPermission("gkcore.command.cloudnet");
    }
}
