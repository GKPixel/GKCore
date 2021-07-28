package gk.minuskube.inv.opener;

import gk.minuskube.inv.ClickableItem;
import gk.minuskube.inv.SmartInventory;
import gk.minuskube.inv.content.InventoryContents;
import me.GK.core.GKCore;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public interface InventoryOpener {

	Inventory update(SmartInventory inv, Player player);
    Inventory open(SmartInventory inv, Player player);
    boolean supports(InventoryType type);

    default void fill(Inventory handle, InventoryContents contents) {
        ClickableItem[][] items = contents.all();

        
        for(int row = 0; row < items.length; row++) {
            for(int column = 0; column < items[row].length; column++) {
                if(items[row][column] != null) {
                    handle.setItem(9 * row + column, items[row][column].getItem());
                }
            }
        }
    }

}
