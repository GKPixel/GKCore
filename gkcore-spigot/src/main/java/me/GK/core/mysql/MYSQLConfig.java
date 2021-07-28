package me.GK.core.mysql;

import me.GK.core.GKCore;

public class MYSQLConfig {
    private static final String host = "host";
    private static final String user = "user";
    private static final String password = "password";
    private static final String database = "database";
    private static final String port = "port";
    private static final String ssl = "use_SSL";

    public static void clear() {
        set(host, "", false);
        set(user, "", false);
        set(password, "", false);
        set(database, "", false);
        set(port, "3306", false);
        set(ssl, true, false);
    }

    public static void create() {
        set(host, "", true);
        set(user, "", true);
        set(password, "", true);
        set(database, "", true);
        set(port, "3306", true);
        set(ssl, true, true);
    }

    public static void reload() {
        GKCore.plugin.reloadConfig();
        MYSQLConfig.create();
    }

    public static String getHost() {
        return MYSQLConfig.get(host);
    }

    public static void setHost(String s) {
        if (!MYSQLConfig.getHost().equalsIgnoreCase(s)) {
            MYSQLConfig.set(host, s, false);
        }
    }

    public static String getUser() {
        return MYSQLConfig.get(user);
    }

    public static void setUser(String s) {
        if (!MYSQLConfig.getUser().equalsIgnoreCase(s)) {
            MYSQLConfig.set(user, s, false);
        }
    }

    public static String getPassword() {
        return MYSQLConfig.get(password);
    }

    public static void setPassword(String s) {
        if (!MYSQLConfig.getPassword().equalsIgnoreCase(s)) {
            MYSQLConfig.set(password, s, false);
        }
    }

    public static String getDatabase() {
        return MYSQLConfig.get(database);
    }

    public static void setDatabase(String s) {
        if (!MYSQLConfig.getDatabase().equalsIgnoreCase(s)) {
            MYSQLConfig.set(database, s, false);
        }
    }

    public static String getPort() {
        return MYSQLConfig.get(port);
    }

    public static void setPort(String s) {
        if (!MYSQLConfig.getPort().equalsIgnoreCase(s)) {
            MYSQLConfig.set(port, s, false);
        }
    }

    public static boolean getSSL() {
        return MYSQLConfig.getBoolean(ssl);
    }

    public static void setSSL(boolean b) {
        if (MYSQLConfig.getSSL() != b) {
            MYSQLConfig.set(ssl, b, false);
        }
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

