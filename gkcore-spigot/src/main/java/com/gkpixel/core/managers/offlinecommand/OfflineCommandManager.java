package com.gkpixel.core.managers.offlinecommand;

import org.bukkit.entity.Player;

/**
 * This is a manager to store command for offline players in database.
 * And execute the command when the player is online
 * <p>
 * Usage:
 * - Automatic online store + reward system
 */
public class OfflineCommandManager {
    /////////////////////////////////////////////////////
    //#region Main function

    /**
     * run all awaiting commands in the database
     *
     * @param player
     * @return
     */
    public static boolean runAllAwaitingCommands(Player player) {
        if (!player.isOnline())
            return false;
        //Sync the data with database
        System.out.println("loading offline command: " + player.getName());
        OfflineCommandDatabase.instance.load(player.getUniqueId().toString(), (result) -> {
            if (result == null) {
                System.out.println("Cannot find player offline command data: " + player.getName());
                //create a new data
                OfflineCommandData newData = new OfflineCommandData(player.getUniqueId());
                OfflineCommandDatabase.instance.addNew(newData);
                return;
            }
            System.out.println("finished loading offline command: " + player.getName());
            OfflineCommandData data = (OfflineCommandData) result;//data parsing
            System.out.println("running all loaded commands for player: " + player.getName());
            data.runAllAwaitingCommands(player);//run all command for players
        });
        return true;
    }

    //#endregion
    /////////////////////////////////////////////////////
}
