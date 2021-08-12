package me.GK.core.modules;

import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import me.GK.core.GKCore;
import me.GK.core.main.Extensions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageSystem {
    public final String DEFAULT_LANGUAGE = "en-US";
    public List<String> languages;
    public HashMap<String, Map<File, Configuration>> message = new HashMap<>();
    Plugin plugin;

    public MessageSystem(Plugin plugin) {
        this.plugin = plugin;
        initiate();
    }

    @SneakyThrows
    public void initiate() {
        languages = GKCore.instance.configSystem.config.getStringList("languages");
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File messagesFolder = new File(String.valueOf(Paths.get(String.valueOf(dataFolder.getAbsoluteFile()), "messages")));
        if (!messagesFolder.exists()) {
            messagesFolder.mkdir();
        }
        boolean overrideMessages = GKCore.instance.configSystem.config.getBoolean("overrideMessages");
        for (String lang : languages) {
            File langFile = new File(String.valueOf(Paths.get(String.valueOf(messagesFolder), lang + ".yml")));
            if (!langFile.exists() || overrideMessages) {
                langFile.createNewFile();
                InputStream is = plugin.getResourceAsStream("messages/" + lang + ".yml");
                OutputStream os = new FileOutputStream(langFile);
                ByteStreams.copy(is, os);
            }
        }
        reload();
    }

    @SneakyThrows
    public void reload() {
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

    public String get(UUID playerUUID, String key) {
        GKPlayer player = GKPlayerDatabase.instance.find(playerUUID.toString());
        if (player == null) {
            return get(DEFAULT_LANGUAGE, key);
        }
        return get(player.selectedLanguage, key);
    }

    public String get(CommandSender sender, String key) {
        GKPlayer player = GKPlayerDatabase.instance.find(ProxyServer.getInstance().getPlayer(sender.getName()).getUniqueId().toString());
        if (player == null) {
            return get(DEFAULT_LANGUAGE, key);
        }
        return get(player.selectedLanguage, key);
    }

    public String get(String lang, String key) {
        return Extensions.color(((Configuration) message.get(lang).values().toArray()[0]).getString(key));
    }

    public String get(String key) {
        return get(DEFAULT_LANGUAGE, key);
    }

    public boolean contains(String lang, String key) {
        return ((Configuration) message.get(lang).values().toArray()[0]).contains(key);
    }

    public void send(CommandSender player, String key) {
        player.sendMessage(TextComponent.fromLegacyText(Extensions.color(
                get(ProxyServer.getInstance().getPlayer(player.getName()).getUniqueId(), key))));
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
