package me.GK.core.modules.storingsystem;

import me.GK.core.GKCore;
import me.GK.core.mysql.MySQL;
import me.GK.core.mysql.SQL;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public abstract class StorableObjectDatabase<T extends StorableObject> {
    public DatabaseType type = DatabaseType.FOLDER;
    ///////////////////////////////////////////////////////////////////////////
    //---GLOBAL Settings---
    public Plugin plugin;
    public String databaseName = "";
    ///////////////
    public boolean finishedLoading = false;
    ///////////////////////////////////////////////////////////////////////////
    //---FOLDER Settings---
    public String folderName = "";
    ///////////////////////////////////////////////////////////////////////////
    //---MYSQL Settings---
    public String tableName = "";
    public HashMap<String, T> database = new HashMap<String, T>();

    public static void debug(Object obj) {
        GKCore.debug(obj);
    }

    //Path and file finding
    public String getFolderPath() {
        return getPlugin().getDataFolder() + File.separator + getFolderName() + File.separator;
    }

    public String getStorableObjectPath(String ID) {
        String path = getFolderPath() + ID + ".json";
        return path;
    }

    public File getStorableObjectFile(String ID) {
        ID.replace(".yml", "");
        String path = getStorableObjectPath(ID);
        File file = new File(path);
        return file;
    }

    ///////////////////////////////////////////////////////////////////////////
    //---SETUP---
    public StorableObjectDatabase<T> setup(String typeStr, Plugin plugin, String databaseName) {
        switch (typeStr.toLowerCase()) {
            case "mysql":
            case "sql":
                setup(DatabaseType.MYSQL, plugin, databaseName);
                break;
            case "folder":
            case "file":
            case "local":
            case "json":
                setup(DatabaseType.FOLDER, plugin, databaseName);
                break;
        }
        return this;
    }

    public StorableObjectDatabase<T> setup(DatabaseType type, Plugin plugin, String databaseName) {
        this.databaseName = databaseName;
        switch (type) {
            case FOLDER:
                setupFolder(plugin, databaseName);
                break;
            case MYSQL:
                setupMySQL(plugin, plugin.getName() + "_" + databaseName);
                break;
        }
        return this;
    }

    public StorableObjectDatabase<T> setupFolder(Plugin plugin, String folderName) {
        type = DatabaseType.FOLDER;
        this.plugin = plugin;
        this.folderName = folderName;
        setupFolder();
        initSaveScheduler();
        return this;
    }

    public StorableObjectDatabase<T> setupMySQL(Plugin plugin, String tableName) {
        type = DatabaseType.MYSQL;
        this.plugin = plugin;
        this.tableName = tableName;
        setupTable();
        initSaveScheduler();
        return this;
    }
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    //Need to be changed
    //(Nothing)
    ///////////////////////////////////////////////////////////////////////////////////
    //Getting
    public String getFolderName() {
        return folderName;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getItemClass() {
        Type sooper = getClass().getGenericSuperclass();
        Type t = ((ParameterizedType) sooper).getActualTypeArguments()[0];
        return (Class<T>) t;
    }

    ///////////////////////////////////////////////////////////////////////////
    //[Setup]
    //---FOLDER---
    private void setupFolder() {
        //System.out.print("setup database folder "+getPlugin().getDataFolder());
        File folder = new File(getPlugin().getDataFolder(), getFolderName());
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    //---MYSQL---
    private void setupTable() {
        MySQL.update("create table " + tableName + " (id varchar(255), data longtext) CHARACTER SET utf8 COLLATE utf8_general_ci;");
        debug("setup table");
    }

    ///////////////////////////////////////////////////////////////////////////
    //[loadAll]
    public boolean isFinishedLoading() {
        return finishedLoading;
    }

    public void reset() {
        finishedLoading = false;
        database.clear();
    }

    public void loadAll() {
        loadAll(null);
    }

    public void loadAll(Runnable callback) {
        switch (type) {
            case FOLDER:
                loadAllByFolder(callback);
                break;
            case MYSQL:
                loadAllByMySQL(callback);
                break;
        }
    }

    //---FOLDER---
    private void loadAllInFolder(String folderName) {
        clear();
        File folder = new File(getPlugin().getDataFolder(), folderName);
        File[] files = folder.listFiles();
        for (File file : files) {
            load(file.getName());
        }
    }

    private void loadAllByFolder(Runnable callback) {
        reset();
        new BukkitRunnable() {

            @Override
            public void run() {
                setupFolder();
                try {
                    loadAllInFolder(getFolderName());
                } catch (IllegalArgumentException | SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finishedLoading = true;
                if (callback != null) callback.run();

            }

        }.runTaskAsynchronously(plugin);
    }

    //---MYSQL---
    private void loadAllByMySQL(Runnable callback) {
        reset();
        new Runnable() {

            @Override
            public void run() {
                setupTable();
                MySQL.connect();
                Statement st = null;
                ResultSet rs = null;
                try {
                    String sql = "SELECT * FROM " + tableName + " WHERE 0='0';";
                    st = MySQL.getConnection().createStatement();
                    rs = st.executeQuery(sql);
                    while (rs.next()) {
                        try {
                            Object json = rs.getObject("data");
                            debug("received json: " + json);
                            T obj = GKCore.instance.jsonSystem.gson.fromJson(json.toString(), getItemClass());
                            add(obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //.createStatement();
                //split here//
                /*
				ArrayList<Object> arrayList = SQL.listGet("data", "0", "=", "0", tableName); // Example
				for(Object json : arrayList) {
					//debug(json);
					debug("received json: "+json);
					T obj = GKCore.instance.jsonSystem.gson.fromJson(json.toString(), getItemClass());
					//StorableObject obj = new Gson().fromJson(json.toString(), getItemClass());
					add((T) obj);
				}*/
                finishedLoading = true;
                if (callback != null) callback.run();
            }

        }.run();
    }

    ///////////////////////////////////////////////////////////////////////////
    //clear the database
    public void clear() {
        database.clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    //[load]
    public void load(String ID) {
        load(ID, null);
    }

    ///////////////////////////////////////////////////////////////////////////

    public void load(String ID, Callback callback) {
        switch (type) {
            case FOLDER:
                loadByFolder(ID, callback);
                break;
            case MYSQL:
                loadByMySQL(ID, callback);
                break;
        }
    }
    ///////////////////////////////////////////////////////////////////////////

    public void loadOrAdd(String ID, T newInstance) {
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (type) {
                    case FOLDER: {
                        loadByFolder(ID, new Callback<T>() {
                            @Override
                            public void run(T obj) {
                                // TODO Auto-generated method stub
                                if (obj == null) addNew(newInstance);

                            }

                        });
                    }
                    break;
                    case MYSQL: {
                        loadByMySQL(ID, new Callback<T>() {
                            @Override
                            public void run(T obj) {
                                // TODO Auto-generated method stub
                                if (obj == null) addNew(newInstance);

                            }

                        });

                    }
                    break;
                }
            }
        }.runTaskAsynchronously(GKCore.plugin);

    }

    private void loadByMySQL(String ID, Callback<T> callback) {

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String sql = "SELECT * FROM " + tableName + " WHERE id='" + ID + "';";
                    MySQL.connect();
                    Statement st = MySQL.getConnection().createStatement();//.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    if (rs.next()) {
                        Object json = rs.getObject("data");
                        if (json != null) {
                            T obj = GKCore.instance.jsonSystem.gson.fromJson(json.toString(), getItemClass());
                            add(obj);

                            debug("received json: " + json);
                            //Run the callback
                            if (callback != null) callback.run(obj);
                        } else {
                            if (MySQL.isConnected()) {
                                debug("received null json when getting: " + ID + " in " + tableName);
                                if (callback != null) callback.run(null);
                            } else {
                                debug("received null json while no sql connection, it is acceptable: ");
                            }


                        }
                    } else {
                        //cannot receive anything
                        if (MySQL.isConnected()) {
                            debug("received null json when getting: " + ID + " in " + tableName);
                            if (callback != null) callback.run(null);
                        } else {
                            debug("received null json while no sql connection, it is acceptable: ");
                        }
                    }
                    rs.close();
                    st.close();
                } catch (Exception e) {
                    debug("exception found, not running callback");
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @SuppressWarnings("unchecked")
    private void loadByFolder(String inputID, Callback<T> callback) {

        new BukkitRunnable() {
            String ID = inputID;

            @Override
            public void run() {
                setupFolder();
                ID = ID.replace(".json", "");
                String path = getStorableObjectPath(ID);
                debug("database loading " + ID);
                File file = new File(path);
                Class<T> itemClass = getItemClass();
                if (!file.exists()) {
                    debug("Cannot load storableObject [Reason: cannot find: " + path + "]");
                    if (callback != null) callback.run(null);
                    return;

                }
                if (itemClass.isAssignableFrom(StorableObject.class)) {
                    debug("Cannot load storableObject [Class " + itemClass + " is not a subclass of StorableObject]");
                    return;
                }
                try {
                    String json = new String(Files.readAllBytes(Paths.get(path)));//reading the file
                    T newObj = GKCore.instance.jsonSystem.gson.fromJson(json, getItemClass());//convert json string to object
                    add(newObj);//add into the temp database
                    if (callback != null) callback.run(newObj);
                    debug("database successfully loaded " + ID);
                    return;
                } catch (IllegalArgumentException | SecurityException | IOException e) {
                    // TODO Auto-generated catch block

                    e.printStackTrace();
                    GKCore.debug(ChatColor.GREEN + "try register your abstract class using JsonSystem.registerAbstractClass<MyClass>(MyClass.getClass())");
                    return;
                }
            }
        }.runTaskAsynchronously(plugin);

    }

    public void unload(String ID) {
        database.remove(ID);
    }

    ///////////////////////////////////////////////////////////////////////////
    //[save]
    public void initSaveScheduler() {
        new BukkitRunnable() {
            public void run() {
                if (finishedLoading) {
                    for (String key : database.keySet()) {
                        T obj = database.get(key);
                        checkSave(obj);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void checkSave(T storableObject) {
        if (storableObject == null) return;
        if (storableObject.needToSave && !storableObject.saving) {
            save(storableObject);
        }
    }

    public void save(T storableObject) {
        save(storableObject, null);
    }

    public void save(T storableObject, Runnable callback) {
        switch (type) {
            case FOLDER:
                saveByFolder(storableObject, callback);
                break;
            case MYSQL:
                saveByMySQL(storableObject, callback);
                break;
        }
    }

    private void saveByFolder(T storableObject, Runnable callback) {
        storableObject.needToSave = false;
        storableObject.saving = true;
        String json = GKCore.instance.jsonSystem.gson.toJson(storableObject, getItemClass());//convert to json string
        debug("saved json: " + json);
        debug("" + ChatColor.GREEN + ChatColor.BOLD + "BUG occurred? Check the @Expose issue!");

        String path = (getStorableObjectPath(storableObject.getID()));
        File file = new File(path);

        PrintWriter pw;
        try {
            pw = new PrintWriter(file);
            pw.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8));
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        storableObject.saving = false;
        if (callback != null) callback.run();
    }

    @SuppressWarnings("deprecation")
    private void saveByMySQL(T storableObject, Runnable callback) {
        if (storableObject == null) return;
        storableObject.needToSave = false;
        storableObject.saving = true;
        Runnable br = new Runnable() {
            public void run() {
                String id = storableObject.getID();
                String json = GKCore.instance.jsonSystem.gson.toJson(storableObject, getItemClass());  //return String (no null object)
                debug("upserted json: " + json);
                debug("" + ChatColor.GREEN + ChatColor.BOLD + "BUG occurred? Check the @Expose issue!");
                //MySQL.update("INSERT INTO "+tableName+" (id, data) VALUES ('"+storableObject.getID()+"', '"+json+"')");
                try {
                    String sql = "SELECT * FROM " + tableName + " WHERE id='" + id + "';";
                    MySQL.connect();
                    Statement st = MySQL.getConnection().createStatement();//.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    if (rs.next()) {
                        MySQL.update("UPDATE " + tableName + " SET data" + "='" + json + "' WHERE id='" + id + "';");
                    } else {
                        MySQL.update("INSERT INTO " + tableName + " VALUES ('" + id + "','" + json + "');");
                        //SQL.insertData(column + ", " + selected, "'" + data + "', '" + object + "'", table);
                    }
                    rs.close();
                    st.close();
                    storableObject.saving = false;
                    if (callback != null) callback.run();

                } catch (Exception exception) {
                    // empty catch block
                    storableObject.saving = false;
                    if (callback != null) callback.run();
                    System.out.print("Error while saving to MySQL :");
                    exception.printStackTrace();
                }
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(plugin, br);

    }

    ///////////////////////////////////////////////////////////////////////////
    //add a storableObject
    @SuppressWarnings("unchecked")
    public void addNew(T storableObject) {
        save(storableObject);
        add(storableObject);
    }

    protected void add(T storableObject) {
        database.put(storableObject.getID(), storableObject);
    }
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    //remove a storableObject
    public void remove(T storableObject) {
        remove(storableObject.getID());
    }

    public void remove(String ID) {
        switch (type) {
            case FOLDER:
                removeFile(ID);
                break;
            case MYSQL:
                removeFromSQL(ID);
                break;
        }
        database.remove(ID);
    }

    @SuppressWarnings("unlikely-arg-type")
    private void removeFile(String ID) {
        getStorableObjectFile(ID).delete();
    }

    private void removeFromSQL(String ID) {
        SQL.deleteData("id", "=", ID, tableName);
    }

    //Error
    public void showNotYetFinishLoadingError(String action) {
        GKCore.debugError(GKCore.instance.configSystem.getMessage("errorNotYetFinishLoading") + " Failed to: " + action);
    }

    ///////////////////////////////////////////////////////////////////////////
    //find a storableObject
    @SuppressWarnings("unchecked")
    public T find(String ID) {
        //load(ID);
        if (!isFinishedLoading()) {
            showNotYetFinishLoadingError("find " + ID);
        }
        return database.get(ID);
    }
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    //check if storableObject exists
    public boolean contains(String ID) {
        if (!isFinishedLoading()) {
            showNotYetFinishLoadingError("check containing " + ID);
        }
        return database.containsKey(ID);
    }

    ///////////////////////////////////////////////////////////////////////////
    //Transfer
    public boolean transfer(DatabaseType type) {
        return transfer(null, type);
    }

    public boolean transfer(String type) {
        return transfer(null, type);
    }

    public boolean transfer(CommandSender sender, String typeStr) {
        switch (typeStr.toLowerCase()) {
            case "mysql":
            case "sql":
                return transfer(sender, DatabaseType.MYSQL);

            case "folder":
            case "file":
            case "local":
            case "json":
                return transfer(sender, DatabaseType.FOLDER);
            default:
                return false;
        }
    }

    @SuppressWarnings("deprecation")
    public boolean transfer(CommandSender sender, DatabaseType type) {
        if (this.type == type) {
            if (sender != null) GKCore.instance.configSystem.show(sender, "databaseTransferFailedBecauseAlreadyIs");
            return false;
        } else {
            new BukkitRunnable() {
                public void run() {
                    loadAll();
                    setup(type, plugin, databaseName);
                    int i = 0;
                    for (String id : database.keySet()) {
                        StorableObject SO = database.get(id);
                        SO.save();//save the object to new DB
                        i++;
                        sender.sendMessage("database transferring: " + id + "  (" + (float) i / database.keySet().size() * 100 + "%)");

                    }
                    sender.sendMessage("database transfer FINISHED");
                }
            }.runTaskAsynchronously(plugin);
            return true;
        }


    }

    public enum DatabaseType {
        FOLDER,
        MYSQL
    }

    public interface Callback<T> {
        void run(T obj);
    }


}