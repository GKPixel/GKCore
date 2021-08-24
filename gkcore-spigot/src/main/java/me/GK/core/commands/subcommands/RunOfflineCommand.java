package me.GK.core.commands.subcommands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import me.GK.core.GKCore;
import me.GK.core.managers.offlinecommand.OfflineCommandData;
import me.GK.core.managers.offlinecommand.OfflineCommandDatabase;
import me.GK.core.modules.Commands.CommandManager;
import me.GK.core.modules.Commands.subcommands.Base;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class RunOfflineCommand extends Base {
    public RunOfflineCommand(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if(args.length<=3){
            return;
        }
        String targetName = args[1];
        int delay = Integer.parseInt(args[2]);

        //joining the command
        String offlineCommand = "";
        for(int i = 3 ; i < args.length ; i++){
            offlineCommand+=args[i]+" ";
        }
        //send command
        String cmd = offlineCommand;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
        if(offlinePlayer==null){
            GKCore.instance.messageSystem.send(sender, "cannotFindPlayer");
        }
        UUID uuid = offlinePlayer.getUniqueId();
        OfflineCommandDatabase.instance.load(uuid.toString(), (result)->{
            OfflineCommandData data = new OfflineCommandData(uuid);//empty data
            if(result != null){
                data = (OfflineCommandData) result;//fetched data
            }
            System.out.println("finished fetching offline command: "+targetName);
            System.out.println("adding command for player: "+targetName);
            data.addAwaitingCommand(delay, cmd);
        });
        String[] result = CloudNetDriver.getInstance().getNodeInfoProvider().sendCommandLine(offlineCommand);

        //success message
        String msg = GKCore.instance.messageSystem.get("offlineCommandAdded");
        msg = msg.replace("%cmd%", offlineCommand);
        msg = msg.replace("%player%", targetName);
        sender.sendMessage(msg);

        //console message
        for(String str : result){
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
