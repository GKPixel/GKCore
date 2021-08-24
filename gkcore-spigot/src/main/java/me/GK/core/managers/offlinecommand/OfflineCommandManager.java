package me.GK.core.managers.offlinecommand;

import me.GK.core.GKCore;
import me.GK.core.main.Extensions;
import me.GK.core.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This is a manager to store command for offline players in database.
 * And execute the command when the player is online
 * 
 * Usage:
 * - Automatic online store + reward system
 */
public class OfflineCommandManager{
    /////////////////////////////////////////////////////
    //#region Main function
    /**
     * run all awaiting commands in the database
     * @param player
     * @return
     */
    public static boolean runAllAwaitingCommands(Player player){
        if(!player.isOnline())
            return false;
        //Sync the data with database
        System.out.println("loading offline command: "+player.getName());
        OfflineCommandDatabase.instance.load(player.getUniqueId().toString(), (result)->{
            if(result == null){
                System.out.println("Cannot find player offline command data: "+player.getName());
                //create a new data
                OfflineCommandData newData = new OfflineCommandData(player.getUniqueId());
                OfflineCommandDatabase.instance.addNew(newData);
                return;
            }
            System.out.println("finished loading offline command: "+player.getName());
            OfflineCommandData data = (OfflineCommandData) result;//data parsing
            System.out.println("running all loaded commands for player: "+player.getName());
            data.runAllAwaitingCommands(player);//run all command for players
        });
        return true;
    }

    //#endregion
    /////////////////////////////////////////////////////
}
