package me.GK.core.commands.subcommands;

import me.GK.core.GKCore;
import me.GK.core.modules.Commands.CommandManager;
import me.GK.core.modules.Commands.subcommands.Base;
import me.GK.core.modules.GKPlayerDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Language extends Base {
    public Language(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (GKCore.instance.configSystem.config.getStringList("languages").contains(args[1])) {
            Player player;
            if (args.length == 3 && sender.hasPermission("gkcore.command.language.changeOthers")) {
                player = Bukkit.getPlayer(args[2]);
            } else {
                player = (Player) sender;
            }
            GKPlayerDatabase.instance.find(player.getUniqueId().toString()).changeSelectedLanguage(args[1]);
            GKCore.instance.messageSystem.send(player, "commands.done");
        } else {
            GKCore.instance.messageSystem.send(sender, "commands.languageNotFound");
        }
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.languageDescription");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return CommandManager.createReturnList(GKCore.instance.configSystem.config.getStringList("languages"), args[1], false);
    }
}
