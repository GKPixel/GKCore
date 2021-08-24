package me.GK.core.managers.offlinecommand;

import me.GK.core.GKCore;
import me.GK.core.modules.storingsystem.StorableObjectDatabase;
import me.GK.core.mysql.MySQL;
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
public class OfflineCommandDatabase extends StorableObjectDatabase<OfflineCommandData> {

    public static OfflineCommandDatabase instance;
    public OfflineCommandDatabase(){
        instance = this;
    }
}
