 package me.GK.core.main;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


import me.GK.core.containers.GKPlayer;
import me.GK.core.containers.InputListener;
import me.GK.core.containers.ItemStackEditor;
import me.GK.core.managers.ItemStackManager;
import me.GK.core.modules.ConfigSystem;
import me.GK.core.mysql.MySQL;
import me.clip.placeholderapi.PlaceholderAPI;


public class commandClass implements CommandExecutor {
	static Plugin plugin = GKCore.instance.plugin;

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length <= 0) {
			GKCore.instance.configSystem.showHelpList(sender);
			return false;
		}
		if (!sender.hasPermission("gkcore.command." + args[0].toLowerCase()))
			return true;
		Player tempPlayer = null;
		UUID uid = null;
		if(sender instanceof Player) {
			tempPlayer = (Player)sender;
			uid = tempPlayer.getUniqueId();
		}
		final Player player = tempPlayer;
		
		switch(args[0].toLowerCase()) {
			case "reload":
			{
				GKCore.instance.configSystem.reload();
				GKCore.instance.updateDebugModeFromConfig();
			}
				break;
			case "runcommandwithinput":
				if(args.length > 2) {
					String targetName = args[1];
					Player target = Bukkit.getPlayer(targetName);
					if(target == null) return false;
					String command = "";
					for(int i = 2 ; i < args.length ; i++) {
						command += args[i]+" ";
					}
					String finalCommand = command;
					sender.sendMessage("final command: "+finalCommand);
					InputListener.create(target.getUniqueId(), new Runnable() {
						@Override
						public void run() {
							String result = InputListener.getInput(target);
							String realCmd = finalCommand;
							realCmd = realCmd.replaceAll("%input%", result);
							realCmd = realCmd.replaceAll("\\{input\\}", result);
							realCmd = realCmd.replaceAll("%player%", target.getName());
							realCmd = realCmd.replaceAll("\\{player\\}", target.getName());
							Extensions.runServerCommand(realCmd);
						}
					}).send();
					
				}
				break;
			case "connectsql":
			case "connectmysql":
			case "reconnectsql":
			case "reconnectmysql":
			{
				if(MySQL.isConnected()) 
					MySQL.reconnect();
				else
					MySQL.connect();
			}
			break;
			case "reloadplugins":
			case "reloadplugin":
			case "reloadallplugins":
			case "reloadallplugin":
			case "reloadall":
			{
				sender.sendMessage("hi");
				Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
				for(Plugin p : plugins) {
					String pName = p.getName();
					if(pName.contains("GK")) {
						plugin.getPluginLoader().disablePlugin(p);
						plugin.getPluginLoader().enablePlugin(p);
					}
				}
			}
			break;
			case "test":
			if(args.length > 1) {
				String time = args[1];
				long millis = Long.parseLong(time);
				sender.sendMessage("period str: "+Extensions.millisToString(millis));
				//sender.sendMessage("exists: "+(Bukkit.getOfflinePlayer(targetName)!=null));
			}
			break;

			case "placeholder": 
			case "testplaceholder": {
				if(args.length > 2) {
					Player target = Bukkit.getPlayer(args[1]);
					String placeholderID = args[2];
					String placeholderResult = PlaceholderAPI.setBracketPlaceholders(target, placeholderID);
					placeholderResult = PlaceholderAPI.setBracketPlaceholders(player, placeholderResult);
					placeholderResult = PlaceholderAPI.setBracketPlaceholders(player, placeholderResult);
					if(placeholderResult.equals("null") || placeholderResult.equals(placeholderID)) {
						player.sendMessage(GKCore.instance.configSystem.getMessage("useBracketWithPlaceholder"));
					}
					player.sendMessage(placeholderResult);
				}else if(args.length > 1){
					String placeholderID = args[1];
					String placeholderResult = PlaceholderAPI.setBracketPlaceholders(player, placeholderID);
					placeholderResult = PlaceholderAPI.setBracketPlaceholders(player, placeholderResult);
					placeholderResult = PlaceholderAPI.setBracketPlaceholders(player, placeholderResult);
					if(placeholderResult.equals("null") || placeholderResult.equals(placeholderID)) {
						player.sendMessage(GKCore.instance.configSystem.getMessage("useBracketWithPlaceholder"));
					}
					player.sendMessage(placeholderResult);
				}
			}
				break;
			case "help":
				GKCore.instance.configSystem.showHelpList(sender);
			break;
			case "version":
				sender.sendMessage("version: "+plugin.getDescription().getVersion());
			break;
			
			case "edititem":
			{
				player.sendMessage("editing");
				GKPlayer GKP = GKPlayer.fromPlayer(player);
				ItemStackEditor.create(player, player.getInventory().getItemInHand(), GKCore.instance.configSystem.getMessage("loreEditor"), new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						player.getInventory().setItemInHand(GKP.itemStackEditor.currentEditingItemStack);
					}
					
				}).send();
			}
				break;
			case "editname":
			case "setname":
			case "setdisplay":
			case "display":
				if(player==null) return false;
				if(args.length > 1) {
					String displayName = args[1];
					displayName = Extensions.color(displayName);
					ItemStack item = player.getInventory().getItemInHand();
					ItemStackManager.setDisplay(item, displayName);
				}
				break;
			
			case "edit":
			case "editlore":
				if(player==null) return false;
				ItemStackManager.editLore(player, player.getInventory().getItemInHand(), new Runnable() {
					ItemStack item = player.getInventory().getItemInHand();
					@Override
					public void run() {
						// TODO Auto-generated method stub
						GKPlayer GKP = GKPlayer.fromUUID(player.getUniqueId());
						List<String> lore = GKP.listEditor.currentEditingList;
						ItemStackManager.setLore(item, lore);
					}
					
				});
				break;
			case "startlisteninginput":
				if(args.length > 1) {
					int lineID = Integer.parseInt(args[1]);
					GKPlayer GKP = GKPlayer.fromUUID(uid);
					GKP.listEditor.startListeningEditInput(lineID);
				}
				break;
			case "changepage":
				if(args.length > 1) {
					int pageID = Integer.parseInt(args[1]);
					GKPlayer GKP = GKPlayer.fromUUID(uid);
					GKP.listDisplayer.ChangePage(pageID);
				}
				break;
			case "editinglistgoup":
				if(args.length > 1) {
					int lineID = Integer.parseInt(args[1]);
					GKPlayer GKP = GKPlayer.fromUUID(uid);
					GKP.listEditor.listGoUp(lineID);
				}
				break;
			case "editinglistgodown":
				if(args.length > 1) {
					int lineID = Integer.parseInt(args[1]);
					GKPlayer GKP = GKPlayer.fromUUID(uid);
					GKP.listEditor.listGoDown(lineID);
				}
				break;

			case "listeditorrunbackcallback":
			{
				GKPlayer GKP = GKPlayer.fromUUID(uid);
				if(GKP!=null)
					if(GKP.listEditor!=null)
						if(GKP.listEditor.backCallback!=null)
							GKP.listEditor.backCallback.run();
			}
				break;
			case "listeditorremoveline":
				if(args.length > 1) {
					int lineID = Integer.parseInt(args[1]);
					GKPlayer GKP = GKPlayer.fromUUID(uid);
					GKP.listEditor.removeLine(lineID);
				}
				break;
			case "itemstackeditorsetdisplay":
			{
				GKPlayer GKP = GKPlayer.fromUUID(uid);
				String currentString = GKP.itemStackEditor.getDisplay();
				InputListener.create(uid, currentString, GKP.itemStackEditor.defaultInputTipString, new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String input = InputListener.getInput(player);
						GKP.itemStackEditor.setDisplay(input);
					}
					
				})
				.send();
			}
			
				break;
			case "itemstackeditoreditlore":
			{
				GKPlayer GKP = GKPlayer.fromUUID(uid);
				GKP.itemStackEditor.editLore();
			}
				break;
			case "copyitemstackeditoreditlore":
			{
				GKPlayer GKP = GKPlayer.fromUUID(uid);
				GKP.listEditor.clipboard = GKP.itemStackEditor.getLore();
				player.sendMessage(GKCore.instance.configSystem.getMessage("copiedLore"));
			}
				break;
			case "pasteitemstackeditoreditlore":
			{
				GKPlayer GKP = GKPlayer.fromUUID(uid);
				GKP.itemStackEditor.setLore(GKP.listEditor.clipboard);
			}
				break;
			case "itemstackeditorsetiteminhand":
			{
				GKPlayer GKP = GKPlayer.fromUUID(uid);
				if(player.getInventory()==null) break;
				if(player.getInventory().getItemInHand()==null) break;
				if(player.getInventory().getItemInHand().getType()==Material.AIR) break;
				GKP.itemStackEditor.setItem(player.getInventory().getItemInHand());
			}
				break;
			case "itemstackeditorgetitem":
			{
				GKPlayer GKP = GKPlayer.fromUUID(uid);
				GKP.itemStackEditor.giveItemStack(player);
				player.sendMessage("got");
			}
				break;
			case "itemstackeditoredit":
			{
				GKPlayer GKP = GKPlayer.fromUUID(uid);
				GKP.itemStackEditor.edit(player);
			}
				break;
			case "itemstackeditorrunbackcallback":
			{
				GKPlayer GKP = GKPlayer.fromUUID(uid);
				if(GKP!=null)
					if(GKP.itemStackEditor!=null)
						if(GKP.itemStackEditor.backCallback!=null)
							GKP.itemStackEditor.backCallback.run();
			}
				break;
		}
	return true;
	
	}
}
