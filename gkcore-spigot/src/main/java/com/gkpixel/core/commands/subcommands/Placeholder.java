package com.gkpixel.core.commands.subcommands;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.modules.Commands.CommandManager;
import com.gkpixel.core.modules.Commands.subcommands.Base;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public class Placeholder extends Base {
    public Placeholder(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Player target;
        String placeholderID;
        if (args.length > 2) {
            target = Bukkit.getPlayer(args[1]);
            placeholderID = args[2];
        } else {
            target = (Player) sender;
            placeholderID = args[1];
        }
        String placeholderResult = PlaceholderAPI.setBracketPlaceholders(target, placeholderID);
        if (placeholderResult == null || placeholderResult.equals(placeholderID)) {
            GKCore.instance.messageSystem.send(sender, "useBracketWithPlaceholder");
        }
        sender.sendMessage(placeholderResult);
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.placeholderDescription");
    }

    @Override
    public boolean canExecute(CommandSender sender, String[] args) {
        return sender.hasPermission("gkcore.command.placeholder");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> playerNames = new LinkedList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return CommandManager.createReturnList(playerNames, args[1], false);
        }
        if (args.length == 3) {
            return CommandManager.createReturnList(new LinkedList<>(PlaceholderAPI.getRegisteredIdentifiers()), args[2], false);
        }
        return super.onTabComplete(sender, args);
    }
}
