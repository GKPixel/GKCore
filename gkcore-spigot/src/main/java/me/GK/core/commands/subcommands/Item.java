package me.GK.core.commands.subcommands;

import me.GK.core.GKCore;
import me.GK.core.containers.GKPlayer;
import me.GK.core.main.Extensions;
import me.GK.core.managers.ItemStackManager;
import me.GK.core.modules.Commands.CommandManager;
import me.GK.core.modules.Commands.subcommands.Base;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Item extends Base {
    public Item(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args[1].toLowerCase(Locale.ROOT).equals("name")) {
            ItemStackManager.setDisplay(((Player) sender).getInventory().getItemInMainHand(), Extensions.color(args[2]));
        }
        if (args[2].toLowerCase(Locale.ROOT).equals("lore")) {
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
        return GKCore.instance.messageSystem.get(sender, "itemDescription");
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
