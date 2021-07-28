package me.GK.core.listeners;

import me.GK.core.GKCore;
import me.GK.core.modules.GKPlayerDatabase;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoinListener implements Listener {
    GKCore plugin;

    public PlayerJoinListener(GKCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerHandshakeEvent event) {
        GKPlayerDatabase.instance.load(ProxyServer.getInstance().getPlayer(event.getConnection().getName()).getUniqueId().toString());
    }
}
