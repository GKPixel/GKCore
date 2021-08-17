package me.GK.core.modules;

import me.GK.core.modules.storingsystem.StorableObjectDatabase;

public class GKPlayerDatabase extends StorableObjectDatabase<GKPlayer> {
    public static GKPlayerDatabase instance;

    public GKPlayerDatabase() {
        instance = this;
    }
}
