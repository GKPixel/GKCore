package com.gkpixel.core.modules;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.main.Extensions;
import com.gkpixel.core.utils.Tuple;
import lombok.SneakyThrows;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
        //If sender is player
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return get(player.getUniqueId(), key);
        }
        //sender is console, then use default
        return get(key);
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

    public String replaceAll(String target, Tuple... replacements) {
        for (Tuple replacement : replacements) {
            target = target.replaceAll(replacement.a.toString(), replacement.b.toString());
        }
        return target;
    }

    public boolean contains(String lang, String key) {
        return ((FileConfiguration) message.get(lang).values().toArray()[0]).contains(key);
    }

    public void send(CommandSender sender, String key) {
        sender.sendMessage(get(sender, key));
    }

    public void send(CommandSender sender, String key, Tuple... replacements) {
        sender.sendMessage(replaceAll(get(sender, key), replacements));
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
