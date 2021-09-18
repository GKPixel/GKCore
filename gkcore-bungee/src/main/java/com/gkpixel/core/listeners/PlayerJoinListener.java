package com.gkpixel.core.listeners;

import com.gkpixel.core.modules.GKPlayerDatabase;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Objects;

public class PlayerJoinListener implements Listener {
    Plugin plugin;

    public PlayerJoinListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PreLoginEvent e) {
        for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
            if (Objects.equals(pp.getName(), e.getConnection().getName())) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        GKPlayerDatabase.instance.load(event.getPlayer().getUniqueId().toString());
    }
}
