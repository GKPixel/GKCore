package me.GK.core.listeners;

import me.GK.core.GKCore;
import me.GK.core.modules.GKPlayerDatabase;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerLeaveListener implements Listener {
    GKCore plugin;

    public PlayerLeaveListener(GKCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        GKPlayerDatabase.instance.load(event.getPlayer().getUniqueId().toString());
    }
}