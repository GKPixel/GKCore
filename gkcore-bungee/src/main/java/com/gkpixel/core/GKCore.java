package com.gkpixel.core;

import com.gkpixel.core.listeners.ListenerRegistry;
import com.gkpixel.core.modules.ConfigSystem;
import com.gkpixel.core.modules.GKPlayerDatabase;
import com.gkpixel.core.modules.JsonSystem;
import com.gkpixel.core.modules.MessageSystem;
import com.gkpixel.core.modules.Version;
import com.gkpixel.core.mysql.MYSQLConfig;
import com.gkpixel.core.mysql.MySQL;
import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class GKCore extends Plugin {
    private static final int maxDebugPerFrame = 10;
    private static final List<String> tempDebugList = new ArrayList<String>();
    public static Plugin plugin;
    public static GKCore instance;
    public static boolean debugging = true;
    public static Version version;
    private static CommandSender consoleSender;
    public JsonSystem jsonSystem = JsonSystem.create();
    public ConfigSystem configSystem;
    public MessageSystem messageSystem;

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

    public void updateDebugModeFromConfig() {
        GKCore.debugging = Boolean.parseBoolean(GKCore.instance.configSystem.get("debugMode"));
    }

    @SuppressWarnings("deprecation")
    public void initiateDebugSystem() {
        updateDebugModeFromConfig();
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            for (int i = 0; i < maxDebugPerFrame; i++) {
                sendOldestDebug();
            }
        }, 0, 1, TimeUnit.SECONDS);
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

    public void initiateMySQL() {
        MYSQLConfig.create();
        MySQL.connect();
        // Start initializing database for GKPlayerDatabase
        new GKPlayerDatabase().setupMySQL(this, "GKCore_players").finishedLoading = true;
        // End
    }

    public void initiate() {
        configSystem = new ConfigSystem(this);
        messageSystem = new MessageSystem(this);
        ListenerRegistry.register(this);
        initiateDebugSystem();
        System.out.print("GKCore verified");
        ProxyServer.getInstance().getScheduler().runAsync(this, this::initiateMySQL);
    }

    @Override
    public void onEnable() {
        plugin = this;
        version = new Version(plugin.getDescription().getVersion());
        instance = this;
        consoleSender = ProxyServer.getInstance().getConsole();
        initiate();
    }

    public void onDisable() {
        instance = null;
    }
}
