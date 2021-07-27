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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageSystem {
    public final String[] languages = {"en-US", "zh-TW"};
    public Dictionary<String, Map<File, Configuration>> message;
    public HashMap<String, String> messages = new HashMap<String, String>();
    public List<String> helpList = new ArrayList<String>();
    Plugin plugin;

    public MessageSystem(Plugin plugin) {
        this.plugin = plugin;
        initiate();
    }

    @SneakyThrows
    public void initiate() {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File messagesFolder = new File(String.valueOf(Paths.get(String.valueOf(dataFolder.getAbsoluteFile()), "messages")));
        if (!messagesFolder.exists()) {
            messagesFolder.mkdir();
        }
        for (String lang : languages) {
            File langFile = new File(String.valueOf(Paths.get(String.valueOf(messagesFolder), lang + ".yml")));
            if (!langFile.exists()) {
                langFile.createNewFile();
                InputStream is = plugin.getResourceAsStream("messages/" + lang);
                OutputStream os = new FileOutputStream(langFile);
                ByteStreams.copy(is, os);
            }
        }
        reload();
    }

    @SneakyThrows
    public void reload() {
        message = null;
        File dataFolder = plugin.getDataFolder();
        File messagesFolder = new File(String.valueOf(Paths.get(String.valueOf(dataFolder.getAbsoluteFile()), "messages")));
        for (String lang : languages) {
            File langFile = new File(String.valueOf(Paths.get(String.valueOf(messagesFolder), lang + ".yml")));
            message.put(lang, new HashMap<File, Configuration>() {
                {
                    put(langFile, ConfigurationProvider.getProvider(YamlConfiguration.class).load(langFile));
                }
            });
        }
    }

    @SneakyThrows
    public void set(String lang, String key, Object value) {
        Map<File, Configuration> setting = message.get(lang);
        File langFile = (File) setting.keySet().toArray()[0];
        message.put(lang, new HashMap<File, Configuration>() {
            {
                put(langFile, ConfigurationProvider.getProvider(YamlConfiguration.class).load(langFile));
            }
        });
        save(lang);
    }

    public String get(String lang, String key) {
        return (((Configuration) message.get(lang).values().toArray()[0]).getString(key));
    }

    public String get(String key) {
        return get("en-US", key);
    }

    public boolean contains(String lang, String key) {
        return ((Configuration) message.get(lang).values().toArray()[0]).contains(key);
    }

    @SneakyThrows
    public void save() {
        for (String lang : languages) {
            save(lang);
        }
    }

    @SneakyThrows
    public void save(String lang) {
        Map<File, Configuration> langDetails = message.get(lang);
        ConfigurationProvider.getProvider(YamlConfiguration.class).save((Configuration) langDetails.values().toArray()[0], (File) langDetails.keySet().toArray()[0]);
    }
}
