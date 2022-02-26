package com.gkpixel.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.gkpixel.core.commands.GKCoreCommands;
import com.gkpixel.core.containers.ListEditor;
import com.gkpixel.core.main.Event;
import com.gkpixel.core.managers.GKPlayerManager;
import com.gkpixel.core.managers.ItemStackManager;
import com.gkpixel.core.managers.offlinecommand.OfflineCommandDatabase;
import com.gkpixel.core.modules.ConfigSystem;
import com.gkpixel.core.modules.GKPlayer;
import com.gkpixel.core.modules.GKPlayerDatabase;
import com.gkpixel.core.modules.JsonSystem;
import com.gkpixel.core.modules.MessageSystem;
import com.gkpixel.core.modules.TextButtonSystem;
import com.gkpixel.core.modules.Version;
import com.gkpixel.core.mysql.MYSQLConfig;
import com.gkpixel.core.mysql.MySQL;
import gk.minuskube.inv.InventoryManager;
import lombok.SneakyThrows;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.FileReader;
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
    public ProtocolManager protocolManager;
    public JsonSystem jsonSystem = JsonSystem.create();
    public ConfigSystem configSystem;
    public MessageSystem messageSystem;
    public InventoryManager invManager;
    public com.gkpixel.core.managers.ItemStackManager ItemStackManager = new ItemStackManager();
    public com.gkpixel.core.managers.GKPlayerManager GKPlayerManager = new GKPlayerManager();

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
        boolean result = ((GKCore.version.isGreaterThan(r1) || GKCore.version.isEquals(r1)) && GKCore.version.isSmallerThan(r2));
        if (result) {
        } else {
            System.out.println("[ERROR] Cannot load " + thisPluginName + ", please use GKCore between " + r1 + "-" + r2 + ", or update both " + thisPluginName + " and GKCore to latest version");

        }
        return result;
    }

    public static String setPlaceholders(Player player, String str) {
        //find the inner brackets, only replace the inner one
        int startBracketIndex = -1;
        int endBracketIndex = -1;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '{') {
                startBracketIndex = i;
            } else if (c == '}') {
                //start replacing
                endBracketIndex = i;
                String part1 = str.substring(0, startBracketIndex);
                String part2 = str.substring(startBracketIndex, endBracketIndex + 1);
                part2 = PlaceholderAPI.setBracketPlaceholders(player, part2);
                String part3 = str.substring(endBracketIndex + 1);
                return part1 + part2 + part3;
            }
        }
        return str;
    }

    public void updateDebugModeFromConfig() {
        GKCore.debugging = Boolean.parseBoolean(GKCore.instance.configSystem.get("debugMode"));
    }

    @SuppressWarnings("deprecation")
    public void initiateDebugSystem() {
        updateDebugModeFromConfig();
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (int i = 0; i < maxDebugPerFrame; i++) {
                sendOldestDebug();
            }
        }, 0, 1);
    }

    @SneakyThrows
    public Properties getServerProperties() {
        BufferedReader is;
        is = new BufferedReader(new FileReader("server.properties"));
        Properties props = new Properties();
        props.load(is);
        is.close();
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
        new GKPlayerDatabase();
        GKPlayerDatabase.instance.setupMySQL(this, "GKCore_players").finishedLoading = true;
        new OfflineCommandDatabase();
        OfflineCommandDatabase.instance.setupMySQL(this, "GKCore_offlineCommands").finishedLoading = true;

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                GKPlayerDatabase.instance.load(player.getUniqueId().toString(), (gkp) -> {
                    if (gkp == null) {
                        GKPlayerDatabase.instance.addNew(new GKPlayer(player.getUniqueId().toString()));
                    }
                });
            }
        });
    }

    public void initProtocolLib() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void initiate() {
        configSystem = new ConfigSystem(this);
        messageSystem = new MessageSystem(this);
        new TextButtonSystem(this);
        GKCoreCommands.register(this);
        System.out.println("GKCore verified");

        //Init debug system
        initiateDebugSystem();

        //InitiateMySQL
        Bukkit.getScheduler().runTaskAsynchronously(this, this::initiateMySQL);

        //Init smart inv
        initiateSmartInvs();

        //Iniit player manager
        com.gkpixel.core.managers.GKPlayerManager.addAllPlayers();
        Bukkit.getPluginManager().registerEvents(new Event(), this);

        //Init List editor
        ListEditor.initiate();

        //init protocollib
        initProtocolLib();

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
