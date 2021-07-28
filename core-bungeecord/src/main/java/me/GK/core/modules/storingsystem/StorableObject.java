package me.GK.core.modules.storingsystem;

public abstract class StorableObject {
    public boolean saving = false;//this mean the file is uploading to mysql or saving to local. Prevent multiple save at the same time
    public boolean needToSave = false;//when use save(). this will become true. wait for next loop then it would upload to database

    ///////////////////////////////////////////////////////////////////////////////////////
    //Changable methods
    public abstract StorableObjectDatabase getDatabase();

    public abstract String getID();
    ///////////////////////////////////////////////////////////////////////////////////////

    //Saving
    public void initiate() {

    }

    public void save() {
        needToSave = true;
    }
    ////////////////////////////////////////////////////////////////////////////////


}
