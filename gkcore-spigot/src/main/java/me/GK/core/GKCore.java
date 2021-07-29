package me.GK.core;

import gk.minuskube.inv.InventoryManager;
import me.GK.core.commands.GKCoreCommands;
import me.GK.core.containers.ListEditor;
import me.GK.core.main.Event;
import me.GK.core.managers.GKPlayerManager;
import me.GK.core.managers.ItemStackManager;
import me.GK.core.modules.ConfigSystem;
import me.GK.core.modules.JsonSystem;
import me.GK.core.modules.MessageSystem;
import me.GK.core.modules.Version;
import me.GK.core.mysql.MYSQLConfig;
import me.GK.core.mysql.MySQL;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class GKCore extends JavaPlugin {
    private static final int maxDebugPerFrame = 10;
    private static final List<String> tempDebugList = new ArrayList<String>();
    public static Plugin plugin;
    public static GKCore instance;
    public static boolean debugging = true;
    public static Player G7 = Bukkit.getPlayer("hiIamG7");
    public static Version version;
    private static ConsoleCommandSender consoleSender;
    public JsonSystem jsonSystem = JsonSystem.create();
    public ConfigSystem configSystem;
    public MessageSystem messageSystem;
    public InventoryManager invManager;
    public ItemStackManager ItemStackManager = new ItemStackManager();
    public GKPlayerManager GKPlayerManager = new GKPlayerManager();

    public static GKCore getInstance() {
        return instance;
    }

    public static void startDebugging() {
        debugging = true;
    }

    public static void endDebugging() {
        debugging = false;
    }

    public static void debug(Object obj) {
        if (debugging)
            tempDebugList.add(obj.toString());//add into the temp list and wait for the queue
    }

    public static void G7_log(Object msg) {
        if (G7 != null) G7.sendMessage(msg.toString());
    }

    public static void forceDebug(Object obj) {
        tempDebugList.add(obj.toString());//add into the temp list and wait for the queue
    }

    public static void debugError(Object obj) {
        forceDebug(obj);
    }

    private static void sendOldestDebug() {//send the oldest
        if (tempDebugList.size() > 0) {
            String debugStr = tempDebugList.get(0);
            consoleSender.sendMessage(debugStr);
            tempDebugList.remove(0);
        }
    }

    public static String getUpdateLink() {
        return "http://gkpixel.com";
    }

    public static boolean versionIsBetween(String thisPluginName, String r1, String r2) {
        boolean result = (GKCore.version.isGreaterThan(r1) && GKCore.version.isSmallerThan(r2));
        if (result) {
        } else {
            System.out.println("[ERROR] Cannot load " + thisPluginName + ", please use GKCore between " + r1 + "-" + r2 + ", or update both " + thisPluginName + " and GKCore to latest version");

        }
        return result;
    }

    public static String setPlaceholders(Player player, String str) {
        return PlaceholderAPI.setBracketPlaceholders(player, str);
    }

    public void updateDebugModeFromConfig() {
        GKCore.debugging = Boolean.parseBoolean(GKCore.instance.configSystem.get("debugMode"));
    }

    @SuppressWarnings("deprecation")
    public void initiateDebugSystem() {
        updateDebugModeFromConfig();
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < maxDebugPerFrame; i++) {
                    sendOldestDebug();
                }
            }
        }, 0, 1);
    }

    public Properties getServerProperties() {
        BufferedReader is = null;
        try {
            is = new BufferedReader(new FileReader("server.properties"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return props;
    }

    public String getProperty(String str) {
        return getServerProperties().getProperty(str);
    }

    public void initiateSmartInvs() {
        invManager = new InventoryManager(this);
        invManager.init();
    }

    public void initiateMySQL() {
        MYSQLConfig.create();
        MySQL.connect();
    }

    public void initiate() {
        configSystem = new ConfigSystem(this);
        messageSystem = new MessageSystem(this);
        GKCoreCommands.register(this);
        initiateDebugSystem();
        System.out.print("GKCore verified");

        Bukkit.getScheduler().runTaskAsynchronously(this, this::initiateMySQL);

        initiateSmartInvs();

        me.GK.core.managers.GKPlayerManager.addAllPlayers();
        Bukkit.getPluginManager().registerEvents(new Event(), this);

        ListEditor.initiate();

    }

    @Override
    public void onEnable() {
        plugin = this;
        version = new Version(plugin.getDescription().getVersion());
        instance = this;
        consoleSender = this.getServer().getConsoleSender();
        initiate();

    }

    public void onDisable() {
        instance = null;
        ItemStackManager = null;
        GKPlayerManager = null;
    }
}
