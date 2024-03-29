package com.gkpixel.core.gui;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.containers.ItemStackEditor;
import com.gkpixel.core.managers.ItemStackManager;
import com.gkpixel.core.modules.ConfigSystem;
import gk.minuskube.inv.ClickableItem;
import gk.minuskube.inv.SmartInventory;
import gk.minuskube.inv.content.InventoryContents;
import gk.minuskube.inv.content.InventoryProvider;
import gk.minuskube.inv.content.Pagination;
import gk.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ButtonEditor implements InventoryProvider {
    public ConfigSystem configSystem;

    public ButtonEditor(ConfigSystem configSystem) {
        this.configSystem = configSystem;
    }

    public static SmartInventory getEditor(ConfigSystem configSystem) {
        return SmartInventory.builder().id("ButtonEditor").provider(new ButtonEditor(configSystem)).size(6, 9)
                .title(GKCore.instance.messageSystem.get("buttonEditorName")).closeable(true).build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        List<ClickableItem> list = new ArrayList<ClickableItem>();
        for (String buttonID : configSystem.buttons.keySet()) {
            ItemStack button = configSystem.buttons.get(buttonID).clone();
            button = ItemStackManager.addLore(button, GKCore.instance.messageSystem.get("buttonIDLore").replace("%buttonID%", buttonID));
            button = ItemStackManager.addLore(button, GKCore.instance.messageSystem.get("leftClickToEdit"));
            button = ItemStackManager.addLore(button, GKCore.instance.messageSystem.get("dragItemToReplace"));

            list.add(ClickableItem.of(button, e -> {
                //Add new gkitem with default item
                if (e.getCursor().getType() != Material.AIR) {
                    configSystem.setButton(buttonID, e.getCursor());
                    player.getInventory().addItem(e.getCursor());
                    player.setItemOnCursor(null);
                    getEditor(configSystem).open(player);
                } else {
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
                            .send();
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