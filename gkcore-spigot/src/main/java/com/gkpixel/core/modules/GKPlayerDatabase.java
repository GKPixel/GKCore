package com.gkpixel.core.modules;

import com.gkpixel.core.modules.storingsystem.StorableObjectDatabase;

public class GKPlayerDatabase extends StorableObjectDatabase<GKPlayer> {
    public static GKPlayerDatabase instance;

    public GKPlayerDatabase() {
        instance = this;
    }
}
