package gk.minuskube.inv.content;

import org.bukkit.entity.Player;

public interface InventoryProvider {

    void init(Player player, InventoryContents contents);

    void update(Player player, InventoryContents contents);

    default void onClose(Player player) {
    }
}
