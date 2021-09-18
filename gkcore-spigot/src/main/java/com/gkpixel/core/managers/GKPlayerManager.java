package com.gkpixel.core.managers;

import com.gkpixel.core.containers.GKPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class GKPlayerManager {
    public static HashMap<UUID, GKPlayer> playerList = new HashMap<UUID, GKPlayer>();

    public static void addAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player.getUniqueId());
        }
    }

    public static GKPlayer addPlayer(UUID uid) {
        GKPlayer gkPlayer = GKPlayer.fromUUID(uid);
        playerList.put(uid, gkPlayer);
        return gkPlayer;
    }

    public static void removePlayer(UUID uid) {
        playerList.remove(uid);
    }

    public static GKPlayer findPlayer(UUID uid) {
        return playerList.get(uid);
    }
}
