package me.GK.core.modules.MessagePackage;

import net.md_5.bungee.api.ProxyServer;

enum ServerPackage {

    MINECRAFT("net.minecraft.server." + getServerVersion()),
    CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion());

    private final String path;

    ServerPackage(String path) {
        this.path = path;
    }

    public static String getServerVersion() {
        return ProxyServer.getInstance().getVersion();
    }

    @Override
    public String toString() {
        return path;
    }

    public Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(this.toString() + "." + className);
    }

}