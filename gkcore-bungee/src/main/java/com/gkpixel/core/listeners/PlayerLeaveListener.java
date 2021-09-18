package com.gkpixel.core.listeners;

import com.gkpixel.core.modules.GKPlayerDatabase;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class PlayerLeaveListener implements Listener {
    Plugin plugin;

    public PlayerLeaveListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        GKPlayerDatabase.instance.unload(event.getPlayer().getUniqueId().toString());
    }
}
