package me.GK.core.containers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import me.GK.core.main.Extensions;
import me.GK.core.main.GKCore;
import me.GK.core.modules.ConfigSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Parse {@link ItemStack} to JSON
 *
 * @author DevSrSouza
 * @version 1.0
 *
 * https://github.com/DevSrSouza/
 * You can find updates here https://gist.github.com/DevSrSouza
 */
public class ListEditor {
	public enum EditorEvent{
		EDIT,
		ADD,
		REMOVE,
		BACK
	}
	public UUID uid;
	public List<String> clipboard = new ArrayList<String>();
	public List<String> currentEditingList = new ArrayList<String>();
	public static String defaultInputTipString = GKCore.instance.configSystem.getMessage("startListeningInput");
	public String editorName = "";
	public String inputTipString = GKCore.instance.configSystem.getMessage("startListeningInput");
	public Runnable savingCallback = null;//the saving callback after clicking the text
	private ClickEvent onEdit = null;//the editing callback after clicking the text
	private ClickEvent onAdd = null;//the editing callback after clicking the add sign
	private ClickEvent onRemove = null;//the editing callback after clicking the remove sign
	private ClickEvent onBack = null;//the editing callback after clicking the back sign
	public Runnable backCallback = null;//the callback runnable
	public int editingLine = -1;
	public ListEditor(UUID uid) {
		this.uid = uid;
	}
	public Player getPlayer() {
		return Bukkit.getPlayer(uid);
	}
	private void debug(String str) {
		////System.out.print(str);
	}
	
	////////////////////////////////////////////////////////////
	//Commands callback
	public void startListeningEditInput(int editingLine) {
		String currentString = "";
		if(currentEditingList.size() > editingLine) {
			currentString = currentEditingList.get(editingLine);
			getPlayer().sendMessage("currentString: "+currentString);
		}
		InputListener.create(uid, currentString, inputTipString, new Runnable() {
			GKPlayer GKP = GKPlayer.fromUUID(uid);
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(editingLine == currentEditingList.size()) currentEditingList.add("");
				currentEditingList.set(editingLine, GKP.inputListener.latestInput);
				//save current list
				savingCallback.run();
				send();
			}
			
		})
		.send();
		this.editingLine = editingLine;
	}
	
	public void listGoUp(int editingLine) {
		debug("received go up command");
		if(editingLine-1 >= 0) {
			String current = currentEditingList.get(editingLine);
			String target = currentEditingList.get(editingLine-1);
			currentEditingList.set(editingLine, target);
			currentEditingList.set(editingLine-1, current);
		}
		savingCallback.run();
		send();
	}
	public void listGoDown(int editingLine) {
		debug("received go down command");
		int length = currentEditingList.size();
		if(editingLine+1 < length) {
			String current = currentEditingList.get(editingLine);
			String target = currentEditingList.get(editingLine+1);
			currentEditingList.set(editingLine, target);
			currentEditingList.set(editingLine+1, current);
		}
		savingCallback.run();
		send();
	}
	public void removeLine(int editingLine) {
		debug("received go down command");
		int length = currentEditingList.size();
		if(editingLine < length) {
			currentEditingList.remove(editingLine);
		}
		savingCallback.run();
		send();
	}
	
	////////////////////////////////////////////////////////////
	

	private void setHoverDescription(BaseComponent component, String str, ChatColor color) {
		ArrayList<BaseComponent> componentList = new ArrayList<BaseComponent>();
		BaseComponent description = new TextComponent(str);
		description.setColor(color);
		componentList.add(description);
		BaseComponent[] baseComponentArray = new BaseComponent[componentList.size()];
		for(int i = 0 ; i < componentList.size() ; i++) {
			baseComponentArray[i] = componentList.get(i);
		}
		component.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT , baseComponentArray));

	}
	
	

	///////////////////////////////////////////////////////////
	//get Components
	private TextComponent getRemoveSignComponent(int lineID) {
		TextComponent remove = new TextComponent(removeSign);
		remove.setColor(ChatColor.RED);
		remove.setBold(true);
		remove.setClickEvent(getClickEvent(EditorEvent.REMOVE,lineID));
		setHoverDescription(remove, "Remove", ChatColor.YELLOW);
		return remove;
	}
	private TextComponent getBackSignComponent() {
		TextComponent remove = new TextComponent(backSign);
		remove.setColor(ChatColor.RED);
		remove.setBold(true);
		remove.setClickEvent(getClickEvent(EditorEvent.BACK,0));
		setHoverDescription(remove, "Back", ChatColor.YELLOW);
		return remove;
	}
	private TextComponent getAddSignComponent(int lineID) {
		TextComponent add = new TextComponent(addSign);
		add.setColor(ChatColor.GREEN);
		add.setBold(true);
		add.setClickEvent(getClickEvent(EditorEvent.ADD,lineID));
		setHoverDescription(add, "Add", ChatColor.YELLOW);
		return add;
	}
	private TextComponent getEditLineComponent(int lineID, String str) {
		TextComponent component = new TextComponent("");
		BaseComponent[] textLine = TextComponent.fromLegacyText(str);
		for(BaseComponent text : textLine) {
			text.setClickEvent(getClickEvent(EditorEvent.EDIT,lineID, str));
			setHoverDescription(text, "Edit", ChatColor.YELLOW);
			component.addExtra(text);
		}
		component.addExtra(getRemoveSignComponent(lineID));
		return component;
	}
	private TextComponent getUpTextComponent(int lineID) {
		TextComponent up = new TextComponent(upSign);
		up.setColor(ChatColor.GOLD);
		up.setBold(true);
		up.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/gk editinglistgoup "+lineID ) );
		setHoverDescription(up, "go up", ChatColor.YELLOW);
		return up;
	}
	private TextComponent getDownTextComponent(int lineID) {
		TextComponent down = new TextComponent(downSign);
		down.setColor(ChatColor.GOLD);
		down.setBold(true);
		down.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/gk editinglistgodown "+lineID ) );
		setHoverDescription(down, "go down", ChatColor.YELLOW);
		return down;
	}
	
	///////////////////////////////////////////////////////////
	

	///////////////////////////////////////////////////////////
	//Sending
	private BaseComponent[] getLine(int lineID, String str) {
		debug("got line");
		BaseComponent text = new TextComponent("");
		BaseComponent up = getUpTextComponent(lineID);
		BaseComponent down = getDownTextComponent(lineID);
		TextComponent line = getEditLineComponent(lineID, str);
		text.addExtra(up);
		text.addExtra(down);
		text.addExtra(" ");
		text.addExtra(line);
		
		return getBaseComponent(text);
	}
	private void sendLine(int lineID, String str) {
		debug("sent line");
		BaseComponent[] text = getLine(lineID, str);
		getPlayer().spigot().sendMessage(text);
				
	}
	private BaseComponent[] getBaseComponent(BaseComponent b) {
		BaseComponent[] result = new BaseComponent[1];
		result[0] = b;
		return result;
	}
	public void sendEditor(Player player) {
		debug("sent editor");
		List<BaseComponent[]> componentList = new ArrayList<BaseComponent[]>();
		componentList.add(getBaseComponent(getBackSignComponent()));
		for(int lineID = 0 ; lineID < currentEditingList.size(); lineID++) {
			String lineString = currentEditingList.get(lineID);
			BaseComponent[] component = getLine(lineID, lineString);
			componentList.add(component);
		}
		componentList.add(getBaseComponent(getAddSignComponent(currentEditingList.size())));
		ListDisplayer.displayList(player, editorName, componentList);
	}
	public void send() {
		sendEditor(getPlayer());
	}
	///////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	//static
	private static String upSign = "[▲]";
	private static String downSign = "[▼]";
	private static String addSign = "[+]";
	private static String removeSign = "  [X]";
	private static String backSign = "[<--]";
	private static String listSign = "-";
	public static void initiate() {
	}
	/////////////////////////////////////////////////////////////////////////


	/////////////////////////////////////////////////////////////////////////
	//useful public static
	/*
	 * Example:
	 * 	ListEditor.create(uid, list, savingCallback).onEdit(editClickEvent).onAdd(addClickEvent).sendEditor();
	 */
	public static ListEditor create(UUID uid , List<String> list, String editorName, String inputTipString, Runnable callback) {
		if(list==null) list = new ArrayList<String>();
		GKPlayer GKP = GKPlayer.fromUUID(uid);
		if(GKP==null) return null;
		GKP.listEditor.resetEvent();
		GKP.listEditor.savingCallback = callback;
		GKP.listEditor.editorName = editorName;
		GKP.listEditor.inputTipString = inputTipString;
		GKP.listEditor.currentEditingList = new ArrayList<String>(list);
		
		return GKP.listEditor;
	}
	public static ListEditor create(UUID uid , List<String> list, String editorName, Runnable callback) {
		return create(uid, list, editorName, defaultInputTipString, callback);
	}
	public static ListEditor create(Player player , List<String> list, String editorName, Runnable callback) {
		UUID uid = player.getUniqueId();
		return create(uid, list, editorName, callback);
	}

	public static ListEditor create(UUID uid , LinkedHashMap<String, String> map, String editorName, String inputTipString, Runnable callback) {
		List<String> list = Extensions.mapToList(map);
		return create(uid, list, editorName, inputTipString, callback);

	}
	public static ListEditor create(UUID uid , LinkedHashMap<String, String> map, String editorName, Runnable callback) {
		return create(uid, map, editorName, defaultInputTipString, callback);
	}
	public static ListEditor create(Player player , LinkedHashMap<String, String> map, String editorName, Runnable callback) {
		return create(player.getUniqueId(), map, editorName, callback);
	}


	public static List<String> getEditingList(Player player) {
		GKPlayer GKP = GKPlayer.fromPlayer(player);
		if(GKP==null) return null;
		return GKP.listEditor.currentEditingList;
	}
	public static LinkedHashMap<String, String> getEditingMap(Player player) {
		return Extensions.listToMap(getEditingList(player));
	}
	/////////////////////////////////////////////////////////////////////////
	//setting event (builder format)
	private void resetEvent() {
		this.onEdit = null;
		this.onAdd = null;
		this.onRemove = null;
		this.onBack = null;
	}
	public ListEditor setTipString(String str) {
		this.inputTipString = str;
		return this;
	}
	public ListEditor onEdit(ClickEvent event) {
		this.onEdit = event;
		return this;
	}
	public ListEditor onAdd(ClickEvent event) {
		this.onAdd = event;
		return this;
	}
	public ListEditor onRemove(ClickEvent event) {
		this.onRemove = event;
		return this;
	}
	public ListEditor onBack(ClickEvent event) {
		this.onBack = event;
		return this;
	}
	public ListEditor onBack(Runnable runnable) {
		this.backCallback = runnable;
		this.onBack = getDefaultEvent(EditorEvent.BACK, -1);
		return this;
	}
	/////////////////////////////////////////////////////////////////////////
	//default Event
	private ClickEvent getClickEvent(EditorEvent eventType, int lineID) {
		return getClickEvent(eventType, lineID, "");
	}
	private ClickEvent getClickEvent(EditorEvent eventType, int lineID, String line) {
		
		switch(eventType) {
			case ADD:
			{
				if(onAdd==null) return getDefaultEvent(eventType, lineID);
				return onAdd;
			}
			case EDIT:
			{
				ClickEvent clickEvent;
				if(onEdit==null) {
					clickEvent = getDefaultEvent(eventType, lineID);
				}else {
					clickEvent = onEdit;
				}
				String newString = clickEvent.getValue().replace("%line%", line).replace("%text%", line).replace("{line}", line).replace("{text}", line);
				ClickEvent result = new ClickEvent(clickEvent.getAction(), newString);
				
				return result;
			}
			case REMOVE:
			{
				ClickEvent clickEvent;
				if(onRemove==null) {
					clickEvent = getDefaultEvent(eventType, lineID);
				}else {
					clickEvent = onRemove;
				}
				String newString = clickEvent.getValue().replace("%line%", line).replace("%text%", line).replace("{line}", line).replace("{text}", line);
				ClickEvent result = new ClickEvent(clickEvent.getAction(), newString);
				
				return result;
			}
			case BACK:
			{
				ClickEvent clickEvent;
				if(onBack==null) {
					clickEvent = getDefaultEvent(eventType, lineID);
				}else {
					clickEvent = onBack;
				}
				
				return clickEvent;
			}
		}
		return null;
	}
	private ClickEvent getDefaultEvent(EditorEvent eventType, int lineID) {
		switch(eventType) {
		case ADD:
			return new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/gk startlisteninginput "+lineID );
		case EDIT:
			return new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/gk startlisteninginput "+lineID );
		case REMOVE:
			return new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/gk listeditorremoveline "+lineID );
		case BACK:
			return new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/gk listEditorRunBackCallback");
		}
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////

}