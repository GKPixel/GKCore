package me.GK.core.modules;

import com.google.gson.annotations.Expose;
import me.GK.core.modules.storingsystem.StorableObject;
import me.GK.core.modules.storingsystem.StorableObjectDatabase;

import java.util.UUID;

public class GKPlayer extends StorableObject {
    @Expose
    public UUID uuid;
    @Expose
    public String selectedLanguage = "en-US";

    public GKPlayer(String ID) {
        this.uuid = UUID.fromString(ID);
    }

    @Override
    public StorableObjectDatabase getDatabase() {
        return GKPlayerDatabase.instance;
    }

    @Override
    public String getID() {
        return uuid.toString();
    }

    public void changeSelectedLanguage(String target) {
        this.selectedLanguage = target;
        save();
    }
}
