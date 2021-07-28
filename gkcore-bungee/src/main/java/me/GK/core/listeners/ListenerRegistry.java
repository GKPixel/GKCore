package me.GK.core.listeners;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class ListenerRegistry {
    public static void register(Plugin plugin) {
        PluginManager pm = plugin.getProxy().getPluginManager();
        pm.registerListener(plugin, new PlayerJoinListener(plugin));
        pm.registerListener(plugin, new PlayerLeaveListener(plugin));
    }
}
