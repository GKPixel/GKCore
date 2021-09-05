package me.GK.core.mysql;

import me.GK.core.GKCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQL {
    private static final boolean sendDebug = GKCore.instance.configSystem.config.getBoolean("debugMode");
    private static Connection con;

    public static void debug(Object obj) {
        if (sendDebug) {
            System.out.println(obj);
        }
    }

    public static Connection getConnection() {
        if (con == null) connect();
        return con;
    }

    public static void setConnection(String host, String user, String password, String database, String port) {
        if (host == null || user == null || password == null || database == null) {
            return;
        }
        MySQL.disconnect(false);
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&useSSL=" + MYSQLConfig.getSSL(), user, password);
            GKCore.debug(GKCore.instance.messageSystem.get("SQLConnected"));
        } catch (Exception e) {
            GKCore.debug(GKCore.instance.messageSystem.get("SQLConnectError") + e.getMessage());
        }
    }

    public static void connect() {
        String host = MYSQLConfig.getHost();
        String user = MYSQLConfig.getUser();
        String password = MYSQLConfig.getPassword();
        String database = MYSQLConfig.getDatabase();
        String port = MYSQLConfig.getPort();
        if (!MySQL.isConnected()) {
            MySQL.setConnection(host, user, password, database, port);
        }
    }

    public static void disconnect() {
        MySQL.disconnect(true);
    }

    private static void disconnect(boolean message) {
        block6:
        {
            try {
                if (MySQL.isConnected()) {
                    con.close();
                    if (message) {
                        GKCore.debug(GKCore.instance.messageSystem.get("SQLdisconnected"));
                    }
                } else if (message) {
                    GKCore.debug(GKCore.instance.messageSystem.get("SQLdisconnectError"));
                }
            } catch (Exception e) {
                if (!message) break block6;
                GKCore.debug(GKCore.instance.messageSystem.get("SQLdisconnectError") + e.getMessage());
            }
        }
        con = null;
    }

    public static void reconnect() {
        MySQL.disconnect();
        MySQL.connect();
    }

    public static boolean isConnected() {
        if (con != null) {
            try {
                return !con.isClosed();
            } catch (Exception e) {
                GKCore.debug(GKCore.instance.messageSystem.get("Error") + e.getMessage());
            }
        }
        return false;
    }

    public static boolean update(String command) {
        boolean result;
        block3:
        {
            if (command == null) {
                return false;
            }
            result = false;
            MySQL.connect();
            try {
                Statement st = MySQL.getConnection().createStatement();
                st.executeUpdate(command);
                st.close();
                debug("---------------------------------------------");
                debug("------------[SUCCESSFULLY UPDATE]------------");
                debug("Command: " + command);
                debug("---------------------------------------------");
                debug("---------------------------------------------");

                result = true;
            } catch (Exception e) {
                String message = e.getMessage();
                if (message == null) break block3;
                debug("---------------------------------------");
                debug("------------[FAILED UPDATE]------------");
                debug("Error: " + message);
                debug("Command: " + command);
                debug("---------------------------------------");
                debug("---------------------------------------");

            }
        }
        MySQL.disconnect(false);
        return result;
    }

    public static ResultSet query(String command) {
        ResultSet rs;
        block3:
        {
            if (command == null) {
                return null;
            }
            MySQL.connect();
            rs = null;
            try {
                PreparedStatement st = MySQL.getConnection().prepareStatement(command);//.createStatement();
                rs = st.executeQuery();
                debug("--------------------------------------------");
                debug("------------[SUCCESSFULLY QUERY]------------");
                debug("Command: " + command);
                debug("--------------------------------------------");
                debug("--------------------------------------------");
            } catch (Exception e) {
                String message = e.getMessage();
                if (message == null) break block3;
                debug("--------------------------------------");
                debug("------------[FAILED QUERY]------------");
                debug("Error: " + message);
                debug("Command: " + command);
                debug("---------------------------------------");
                debug("---------------------------------------");
            }
        }
        return rs;
    }

}

