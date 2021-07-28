package me.GK.core.containers;

import me.GK.core.managers.GKPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GKPlayer {
    public UUID uid;
    public ListEditor listEditor;
    public ItemStackEditor itemStackEditor;
    public ListDisplayer listDisplayer;
    public InputListener inputListener;

    public GKPlayer(UUID uid) {
        this.uid = uid;
        listEditor = new ListEditor(uid);
        itemStackEditor = new ItemStackEditor(uid);
        listDisplayer = new ListDisplayer(uid);
        inputListener = new InputListener(uid);
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

    public ListEditor GetListEditor() {
        return listEditor;
    }
}