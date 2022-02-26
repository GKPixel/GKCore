package com.gkpixel.core.main;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.containers.GKPlayer;
import com.gkpixel.core.managers.GKPlayerManager;
import com.gkpixel.core.managers.offlinecommand.OfflineCommandManager;
import com.gkpixel.core.modules.GKPlayerDatabase;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
                GKPlayerDatabase.instance.addNew(new com.gkpixel.core.modules.GKPlayer(uid.toString()));
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

    public void fixLevitationBug(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (!player.hasPotionEffect(PotionEffectType.LEVITATION)) return;
            //player.sendMessage("has potion");
            PotionEffect effect = player.getPotionEffect(PotionEffectType.LEVITATION);
            if (effect == null) return;
            //player.sendMessage("has potion effect");
            if (effect.getAmplifier() > 127) {
                //player.sendMessage("has potion effect > 127");
                //slow falling
                double originalValue = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue();
                //player.sendMessage("original value: "+originalValue);
                if (originalValue < 1.0) {
                    //player.sendMessage("original value: < 1.0");
                    player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(originalValue);
                        }
                    }.runTaskLater(GKCore.instance, 2);
                }
            }
        }
    }

    @EventHandler
    public void onEntityHurt(EntityDamageEvent event) {
        //f(event.getEntity() instanceof Player)
        //Bukkit.broadcastMessage("on entity hurt");
        //fixLevitationBug(event.getEntity());
    }

    @EventHandler
    public void onEntityHurt2(EntityDamageByBlockEvent event) {
        //if(event.getEntity() instanceof Player)
        //Bukkit.broadcastMessage("on entity hurt 2");
        //fixLevitationBug(event.getEntity());
    }

    @EventHandler
    public void onEntityHurt3(EntityDamageByEntityEvent event) {
        //if(event.getEntity() instanceof Player)
        //Bukkit.broadcastMessage("on entity hurt 3");
        //fixLevitationBug(event.getEntity());
    }

}
