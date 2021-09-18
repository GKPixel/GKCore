package com.gkpixel.core.containers;

import com.gkpixel.core.main.Extensions;
import com.gkpixel.core.managers.GKPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GKPlayer {
    public UUID uid;
    public ListEditor listEditor;
    public ItemStackEditor itemStackEditor;
    public ListDisplayer listDisplayer;
    public InputListener inputListener;
    public long lastJoinTime;
    public Map<String, String> data = new HashMap<>();
    public List<BukkitTask> offlineCancelTaskList = new ArrayList<>();

    public GKPlayer(UUID uid) {
        this.uid = uid;
        listEditor = new ListEditor(uid);
        itemStackEditor = new ItemStackEditor(uid);
        listDisplayer = new ListDisplayer(uid);
        inputListener = new InputListener(uid);
        setLastJoinTime();
    }

    public static GKPlayer fromUUID(UUID uid) {
        GKPlayer GKP = GKPlayerManager.findPlayer(uid);
        if (GKP != null) return GKP;
        return new GKPlayer(uid);
    }

    public static GKPlayer fromPlayer(Player player) {
        UUID uid = player.getUniqueId();
        return fromUUID(uid);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    public void cancelTaskWhenOffline(BukkitTask task) {
        offlineCancelTaskList.add(task);
    }

    public void cancelAllBukkitTasks() {
        System.out.println("canceld all task for " + uid);
        for (BukkitTask task : offlineCancelTaskList) {
            task.cancel();
        }
        offlineCancelTaskList = new ArrayList();
    }

    public ListEditor GetListEditor() {
        return listEditor;
    }

    public long getLastJoinTime() {
        return lastJoinTime;
    }

    public void setLastJoinTime(long lastJoinTime) {
        this.lastJoinTime = lastJoinTime;
    }

    public void setLastJoinTime() {
        setLastJoinTime(Extensions.getCurrentUnixTime());
    }

    public long getJoinedSeconds() {
        return Extensions.getCurrentUnixTime() - getLastJoinTime();
    }

    public long getJoinedTicks() {
        long joinedTicks = getJoinedSeconds() * 20;
        return joinedTicks;

    }
}