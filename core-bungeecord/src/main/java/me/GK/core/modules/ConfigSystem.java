package me.GK.core.modules;

import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ConfigSystem {
    public Configuration config;
    public File configFile;
    Plugin plugin;

    public ConfigSystem(Plugin plugin) {
        this.plugin = plugin;
        initiate();
    }

    @SneakyThrows
    public void initiate() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.createNewFile();
            InputStream is = plugin.getResourceAsStream("config.yml");
            OutputStream os = new FileOutputStream(configFile);
            ByteStreams.copy(is, os);
        }
        reload();
    }

    @SneakyThrows
    public void reload() {
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }

    public void set(String key, Object value) {
        config.set(key, value);
        save();
    }

    public String get(String key) {
        return config.getString(key);
    }

    public boolean contains(String key) {
        return config.contains(key);
    }

    @SneakyThrows
    public void save() {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
    }
}
