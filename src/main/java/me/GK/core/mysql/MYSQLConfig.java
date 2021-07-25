package me.GK.core.mysql;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import me.GK.core.main.GKCore;

public class MYSQLConfig {
    private static final String host = "host";
    private static final String user = "user";
    private static final String password = "password";
    private static final String database = "database";
    private static final String port = "port";
    private static final String ssl = "use_SSL";

    public static void clear() {
        MYSQLConfig.set(host, "", false);
        MYSQLConfig.set(user, "", false);
        MYSQLConfig.set(password, "", false);
        MYSQLConfig.set(database, "", false);
        MYSQLConfig.set(port, "3306", false);
        MYSQLConfig.set(ssl, true, false);
    }

    public static void create() {
        MYSQLConfig.set(host, "", true);
        MYSQLConfig.set(user, "", true);
        MYSQLConfig.set(password, "", true);
        MYSQLConfig.set(database, "", true);
        MYSQLConfig.set(port, "3306", true);
        MYSQLConfig.set(ssl, true, true);
    }

    public static void reload() {
        GKCore.plugin.reloadConfig();
        MYSQLConfig.create();
    }

    public static void setHost(String s) {
        if (!MYSQLConfig.getHost().equalsIgnoreCase(s)) {
            MYSQLConfig.set(host, s, false);
        }
    }

    public static void setUser(String s) {
        if (!MYSQLConfig.getUser().equalsIgnoreCase(s)) {
            MYSQLConfig.set(user, s, false);
        }
    }

    public static void setPassword(String s) {
        if (!MYSQLConfig.getPassword().equalsIgnoreCase(s)) {
            MYSQLConfig.set(password, s, false);
        }
    }

    public static void setDatabase(String s) {
        if (!MYSQLConfig.getDatabase().equalsIgnoreCase(s)) {
            MYSQLConfig.set(database, s, false);
        }
    }

    public static void setPort(String s) {
        if (!MYSQLConfig.getPort().equalsIgnoreCase(s)) {
            MYSQLConfig.set(port, s, false);
        }
    }

    public static void setSSL(boolean b) {
        if (MYSQLConfig.getSSL() != b) {
            MYSQLConfig.set(ssl, b, false);
        }
    }

    public static String getHost() {
        return MYSQLConfig.get(host);
    }

    public static String getUser() {
        return MYSQLConfig.get(user);
    }

    public static String getPassword() {
        return MYSQLConfig.get(password);
    }

    public static String getDatabase() {
        return MYSQLConfig.get(database);
    }

    public static String getPort() {
        return MYSQLConfig.get(port);
    }

    public static boolean getSSL() {
        return MYSQLConfig.getBoolean(ssl);
    }

    private static void set(String name, Object value, boolean checkIfExists) {
    	if (name == null || value == null || checkIfExists && GKCore.instance.configSystem.config.contains(name)) {
            return;
        }
        GKCore.instance.configSystem.set(name, value);
        GKCore.instance.configSystem.save();
    }

    private static String get(String name) {
        return name == null || !GKCore.instance.configSystem.contains(name) ? "" : GKCore.instance.configSystem.config.getString(name);
    }

    private static boolean getBoolean(String name) {
        return name != null && GKCore.instance.configSystem.contains(name) && GKCore.instance.configSystem.config.getBoolean(name);
    }
}

