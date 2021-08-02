package me.GK.core.modules;

import lombok.SneakyThrows;
import me.GK.core.GKCore;
import me.GK.core.main.Extensions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageSystem {
    public final String DEFAULT_LANGUAGE = "en-US";
    public List<String> languages;
    public HashMap<String, Map<File, FileConfiguration>> message = new HashMap<>();
    JavaPlugin plugin;

    public MessageSystem(JavaPlugin plugin) {
        this.plugin = plugin;
        initiate();
    }

    @SneakyThrows
    public void initiate() {
        languages = GKCore.instance.configSystem.config.getStringList("languages");
        reload();
    }

    @SneakyThrows
    public void reload() {
        File dataFolder = plugin.getDataFolder();
        File messagesFolder = new File(String.valueOf(Paths.get(String.valueOf(dataFolder.getAbsoluteFile()), "messages")));
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        if (!messagesFolder.exists()) {
            messagesFolder.mkdir();
        }
        boolean overrideMessages = GKCore.instance.configSystem.config.getBoolean("overrideMessages");
        for (String lang : languages) {
            File langFile = new File(String.valueOf(Paths.get(String.valueOf(messagesFolder), lang + ".yml")));
            if ((!langFile.exists()) || overrideMessages) {
                try {
                    plugin.saveResource("messages/" + lang + ".yml", overrideMessages);
                } catch (IllegalArgumentException e) {
                    // use default if not found
                    langFile = new File(String.valueOf(Paths.get(String.valueOf(messagesFolder), DEFAULT_LANGUAGE + ".yml")));
                }
            }
            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            langConfig.options().copyDefaults(true);
            File finalLangFile = langFile;
            message.put(lang, new HashMap<File, FileConfiguration>() {
                {
                    put(finalLangFile, langConfig);
                }
            });
        }
    }

    @SneakyThrows
    public void set(String lang, String key, Object value) {
        Map<File, FileConfiguration> setting = message.get(lang);
        File langFile = (File) setting.keySet().toArray()[0];
        message.put(lang, new HashMap<File, FileConfiguration>() {
            {
                put(langFile, YamlConfiguration.loadConfiguration(langFile));
            }
        });
        save(lang);
    }

    public String get(UUID playerUUID, String key) {
        GKPlayer player = GKPlayerDatabase.instance.find(playerUUID.toString());
        if (player == null) {
            return get(DEFAULT_LANGUAGE, key);
        }
        return get(player.selectedLanguage, key);
    }

    public String get(CommandSender sender, String key) {
        GKPlayer player = GKPlayerDatabase.instance.find(Bukkit.getPlayer(sender.getName()).getUniqueId().toString());
        if (player == null) {
            return get(DEFAULT_LANGUAGE, key);
        }
        return get(player.selectedLanguage, key);
    }

    public String get(String lang, String key) {
        String result = (((FileConfiguration) message.get(lang).values().toArray()[0]).getString(key));
        if (result != null) {
            return Extensions.color(result);
        } else {
            return key;
        }
    }

    public String get(String key) {
        return get(DEFAULT_LANGUAGE, key);
    }

    public boolean contains(String lang, String key) {
        return ((FileConfiguration) message.get(lang).values().toArray()[0]).contains(key);
    }

    public void send(CommandSender player, String key) {
        player.sendMessage(Extensions.color(get(Bukkit.getPlayer(player.getName()).getUniqueId(), key)));
    }

    @SneakyThrows
    public void save() {
        for (String lang : languages) {
            save(lang);
        }
    }

    @SneakyThrows
    public void save(String lang) {
        Map<File, FileConfiguration> langDetails = message.get(lang);
        ((FileConfiguration) langDetails.values().toArray()[0]).save((File) langDetails.keySet().toArray()[0]);
    }
}
