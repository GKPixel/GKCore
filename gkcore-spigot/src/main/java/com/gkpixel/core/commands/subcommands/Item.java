package com.gkpixel.core.commands.subcommands;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.containers.GKPlayer;
import com.gkpixel.core.main.Extensions;
import com.gkpixel.core.managers.ItemStackManager;
import com.gkpixel.core.modules.Commands.CommandManager;
import com.gkpixel.core.modules.Commands.subcommands.Base;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class Item extends Base {
    public Item(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args[1].toLowerCase(Locale.ROOT).equals("name")) {
            StringJoiner joiner = new StringJoiner(" ");
            for (String cs : Arrays.asList(args).subList(2, args.length)) {
                joiner.add(cs);
            }
            ItemStackManager.setDisplay(((Player) sender).getInventory().getItemInMainHand(),
                    Extensions.color(joiner.toString()));
        }
        if (args[1].toLowerCase(Locale.ROOT).equals("lore")) {
            Player player = (Player) sender;
            ItemStackManager.editLore(player, player.getInventory().getItemInMainHand(), () -> {
                GKPlayer GKP = GKPlayer.fromUUID(player.getUniqueId());
                List<String> lore = GKP.listEditor.currentEditingList;
                ItemStackManager.setLore(player.getInventory().getItemInMainHand(), lore);
            });
        }
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.itemDescription");
    }

    @Override
    public boolean canExecute(CommandSender sender, String[] args) {
        return sender.hasPermission("gkcore.command.item");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CommandManager.createReturnList(Arrays.asList("name", "lore"), args[1], false);
        }
        return super.onTabComplete(sender, args);
    }
}
