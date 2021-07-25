package me.GK.core.main;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.GK.core.containers.GKPlayer;
import me.GK.core.managers.GKPlayerManager;

public class Event implements Listener{
	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uid = player.getUniqueId();
		GKPlayerManager.addPlayer(uid);
	}
	@EventHandler
	public void OnPlayerDisconnect(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uid = player.getUniqueId();
		GKPlayerManager.removePlayer(uid);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		String msg = Extensions.color(e.getMessage());
		GKPlayer GKP = GKPlayer.fromPlayer(e.getPlayer());
		if (GKP==null) return;
		if(GKP.inputListener.checkInput(msg)) {
			e.setCancelled(true);
		}
	}
}
