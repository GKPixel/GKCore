package me.GK.core.main;

import me.GK.core.containers.GKPlayer;
import me.GK.core.managers.GKPlayerManager;
import me.GK.core.managers.offlinecommand.OfflineCommandManager;
import me.GK.core.modules.GKPlayerDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class Event implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uid = player.getUniqueId();

        /////////////////////////////////////////////////////////////////
        //#region GKPlayer
        GKPlayer gkplayer = GKPlayerManager.addPlayer(uid);
        gkplayer.setLastJoinTime(Extensions.getCurrentUnixTime());
        GKPlayerDatabase.instance.load(uid.toString(), (gkp) -> {
            if (gkp == null) {
                GKPlayerDatabase.instance.addNew(new me.GK.core.modules.GKPlayer(uid.toString()));
            }
        });
        //#endregion
        /////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////
        //#region Offline commands
        OfflineCommandManager.runAllAwaitingCommands(player);
        //#endregion
        /////////////////////////////////////////////////////////////////
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uid = player.getUniqueId();

        //cancel all bukkit tasks when offline
        GKPlayer gkp = GKPlayer.fromPlayer(player);
        gkp.cancelAllBukkitTasks();

        GKPlayerManager.removePlayer(uid);
        GKPlayerDatabase.instance.unload(uid.toString());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String msg = Extensions.color(e.getMessage());
        GKPlayer GKP = GKPlayer.fromPlayer(e.getPlayer());
        if (GKP == null) return;
        if (GKP.inputListener.checkInput(msg)) {
            e.setCancelled(true);
        }
    }
}
