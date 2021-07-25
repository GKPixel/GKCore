package me.GK.core.containers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.GK.core.main.GKCore;
import me.GK.core.managers.GKPlayerManager;

public class GKPlayer {
	public UUID uid ;
	public ListEditor listEditor;
	public ItemStackEditor itemStackEditor;
	public ListDisplayer listDisplayer;
	public InputListener inputListener;
	public Map<String, String> data = new HashMap<String, String>();
	
	public GKPlayer(UUID uid) {
		this.uid = uid;
		listEditor = new ListEditor(uid);
		itemStackEditor = new ItemStackEditor(uid);
		listDisplayer = new ListDisplayer(uid);
		inputListener = new InputListener(uid);
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uid);
	}
	public ListEditor GetListEditor() {
		return listEditor;
	}
	
	
	private void debug(String str) {
		//System.out.print(str);
	}
	public static GKPlayer fromUUID(UUID uid) {
		GKPlayer GKP = GKPlayerManager.findPlayer(uid);
		if(GKP!=null) return GKP;
		return new GKPlayer(uid);
	}
	public static GKPlayer fromPlayer(Player player) {
		UUID uid = player.getUniqueId();
		return fromUUID(uid);
	}
	
	
}