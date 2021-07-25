package me.GK.core.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import gk.minuskube.inv.ClickableItem;
import gk.minuskube.inv.SmartInventory;
import gk.minuskube.inv.content.InventoryContents;
import gk.minuskube.inv.content.InventoryProvider;
import gk.minuskube.inv.content.Pagination;
import gk.minuskube.inv.content.SlotIterator;
import me.GK.core.containers.ItemStackEditor;
import me.GK.core.containers.ListEditor;
import me.GK.core.main.GKCore;
import me.GK.core.managers.ItemStackManager;
import me.GK.core.modules.ConfigSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class ButtonEditor implements InventoryProvider {
	public ConfigSystem configSystem;
	public ButtonEditor(ConfigSystem configSystem) {
		this.configSystem = configSystem;
	}

	public static SmartInventory getEditor(ConfigSystem configSystem) {
		return SmartInventory.builder().id("ButtonEditor").provider(new ButtonEditor(configSystem)).size(6, 9)
		.title(GKCore.instance.configSystem.getMessage("buttonEditorName")).closeable(true).build();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		Pagination pagination = contents.pagination();
		List<ClickableItem> list = new ArrayList<ClickableItem>();
		for (String buttonID : configSystem.buttons.keySet()) {
			ItemStack button = configSystem.buttons.get(buttonID).clone();
			button = ItemStackManager.addLore(button, GKCore.instance.configSystem.getMessage("buttonIDLore").replace("%buttonID%", buttonID));
			button = ItemStackManager.addLore(button, GKCore.instance.configSystem.getMessage("leftClickToEdit"));
			button = ItemStackManager.addLore(button, GKCore.instance.configSystem.getMessage("dragItemToReplace"));

			list.add(ClickableItem.of(button, e -> {
				//Add new gkitem with default item
				if(e.getCursor().getType()!=Material.AIR) {
					configSystem.setButton(buttonID, e.getCursor());
					player.getInventory().addItem(e.getCursor());
					player.setItemOnCursor(null);
					getEditor(configSystem).open(player);
				}else {
					player.closeInventory();
					ItemStackEditor.create(player, configSystem.buttons.get(buttonID).clone(), buttonID, new Runnable() {

						@Override
						public void run() {
							configSystem.setButton(buttonID, ItemStackEditor.getEditingItemStack(player));
							player.getInventory().addItem(e.getCursor());
							player.setItemOnCursor(null);
						}
						
					})
					.onBack(new Runnable() {

						@Override
						public void run() {
							getEditor(configSystem).open(player);
						}
						
					})
					.send();;
				}
				
			}));
		}

		ClickableItem[] items = new ClickableItem[list.size()];
		for (int i = 0; i < list.size(); i++) {
			items[i] = list.get(i);
		}
		pagination.setItems(items);
		pagination.setItemsPerPage(45);

		pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

		contents.set(5, 0, ClickableItem.of(GKCore.instance.configSystem.getButton("Back"),
				e -> player.closeInventory()));
		contents.set(5, 3, ClickableItem.of(GKCore.instance.configSystem.getButton("PreviousPage"),
				e -> getEditor(configSystem).open(player, pagination.previous().getPage())));
		contents.set(5, 5, ClickableItem.of(GKCore.instance.configSystem.getButton("NextPage"),
				e -> getEditor(configSystem).open(player, pagination.next().getPage())));

	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}

}