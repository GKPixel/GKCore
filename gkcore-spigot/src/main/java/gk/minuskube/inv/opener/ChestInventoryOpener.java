package gk.minuskube.inv.opener;

import com.gkpixel.core.GKCore;
import com.google.common.base.Preconditions;
import gk.minuskube.inv.InventoryManager;
import gk.minuskube.inv.SmartInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestInventoryOpener implements InventoryOpener {

    public Inventory update(SmartInventory inv, Player player) {
        Preconditions.checkArgument(inv.getColumns() == 9,
                "The column count for the chest inventory must be 9, found: %s.", inv.getColumns());
        Preconditions.checkArgument(inv.getRows() >= 1 && inv.getRows() <= 6,
                "The row count for the chest inventory must be between 1 and 6, found: %s", inv.getRows());
        InventoryManager manager = inv.getManager();
        Inventory topInv = player.getOpenInventory().getTopInventory();
        topInv.setContents(new ItemStack[topInv.getSize()]);
        fill(topInv, manager.getContents(player).get());
        return topInv;
    }

    @Override
    public Inventory open(SmartInventory inv, Player player) {
        Preconditions.checkArgument(inv.getColumns() == 9,
                "The column count for the chest inventory must be 9, found: %s.", inv.getColumns());
        Preconditions.checkArgument(inv.getRows() >= 1 && inv.getRows() <= 6,
                "The row count for the chest inventory must be between 1 and 6, found: %s", inv.getRows());

        InventoryManager manager = inv.getManager();
        Inventory handle = Bukkit.createInventory(player, inv.getRows() * inv.getColumns(), inv.getTitle());


        fill(handle, manager.getContents(player).get());
        new BukkitRunnable() {
            @Override
            public void run() {
                player.openInventory(handle);
            }
        }.runTask(GKCore.instance);
        return handle;
    }

    @Override
    public boolean supports(InventoryType type) {
        return type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST;
    }

}
