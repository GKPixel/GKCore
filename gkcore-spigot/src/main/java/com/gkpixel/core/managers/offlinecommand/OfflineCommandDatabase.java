package com.gkpixel.core.managers.offlinecommand;

import com.gkpixel.core.modules.storingsystem.StorableObjectDatabase;

/**
 * This is a manager to store command for offline players in database.
 * And execute the command when the player is online
 * <p>
 * Usage:
 * - Automatic online store + reward system
 */
public class OfflineCommandDatabase extends StorableObjectDatabase<OfflineCommandData> {

    public static OfflineCommandDatabase instance;

    public OfflineCommandDatabase() {
        instance = this;
    }
}
