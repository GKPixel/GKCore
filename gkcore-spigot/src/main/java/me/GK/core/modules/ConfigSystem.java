package me.GK.core.modules;

import me.GK.core.GKCore;
import me.GK.core.containers.ListDisplayer;
import me.GK.core.gui.ButtonEditor;
import me.GK.core.main.Extensions;
import me.GK.core.managers.ItemStackManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConfigSystem {
    public FileConfiguration config;
    public HashMap<String, String> messages = new HashMap<String, String>();
    public HashMap<String, ItemStack> buttons = new HashMap<String, ItemStack>();
    public List<String> helpList = new ArrayList<String>();
    Plugin plugin;

    public ConfigSystem(Plugin plugin) {
        this.plugin = plugin;
        initiate();
    }

    public String color(String str) {
        return Extensions.color(str);
    }

    public void initiate() {
        plugin.saveDefaultConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        reload();

    }

    private ConfigurationSection GetSectionSafely(ConfigurationSection currentSection, String sectionName) {
        if (currentSection.contains(sectionName)) return currentSection.getConfigurationSection(sectionName);
        return currentSection.createSection(sectionName);
    }

    private void loadAllMessages() {
        messages.clear();
        ConfigurationSection messageConfig = config.getConfigurationSection("Messages");
        for (String key : messageConfig.getKeys(false)) {
            String value = Extensions.color(messageConfig.getString(key));
            messages.put(key, value);
            GKCore.debug(ChatColor.GREEN + "[Config] " + ChatColor.WHITE + key + " : " + value);
        }
    }

    private void loadAllHelps() {
        helpList.clear();
        helpList = config.getStringList("helpList");
        for (int i = 0; i < helpList.size(); i++) {
            helpList.set(i, Extensions.color(helpList.get(i)));
        }
    }

    private void loadAllButtons() {
        buttons.clear();
        ConfigurationSection buttonConfig = GetSectionSafely(config, "Buttons");
        for (String key : buttonConfig.getKeys(false)) {
            ItemStack button = buttonConfig.getItemStack(key);
            buttons.put(key, button);
            System.out.println("loading button: " + key);
        }
    }


    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadAllMessages();
        loadAllHelps();
        loadAllButtons();
    }

    public String getMessage(String messageKey) {
        String result = "";
        result = messages.get(messageKey);
        if (result != null) return result;
        System.out.print("cannot find message key: " + messageKey);
        return messageKey;
    }

    public void set(String key, Object value) {
        config.set(key, value);
        save();
    }

    public void editButtons(Player player) {
        ButtonEditor.getEditor(this).open(player);
    }

    public void setButton(String buttonID, ItemStack itemStack) {
        saveButtonInConfig(buttonID, itemStack);
    }

    public void saveButtonInConfig(String buttonID, ItemStack itemStack) {
        ItemStack newItemStack = itemStack.clone();
        ConfigurationSection section = config.getConfigurationSection("Buttons");
        if (section != null) section.set(buttonID, itemStack);
        buttons.put(buttonID, newItemStack);
        save();
    }

    public ItemStack getButton(String buttonID) {
        ItemStack button = buttons.get(buttonID);
        if (button == null) {
            button = ItemStackManager.setDisplay((new ItemStack(Material.CHEST)), buttonID);
            buttons.put(buttonID, button);
            saveButtonInConfig(buttonID, button);
        }
        return button.clone();
    }

    public String get(String key) {
        return config.getString(key);
    }

    public boolean contains(String key) {
        return config.contains(key);
    }

    public void showHelpList(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<BaseComponent[]> componentList = new ArrayList<BaseComponent[]>();
            for (String line : helpList) {
                String str = line.split(ChatColor.GRAY + "")[0];
                str = ChatColor.stripColor(str);
                BaseComponent[] component = TextButtonSystem.generateTextButton(player, line, new ClickEvent(Action.SUGGEST_COMMAND, str), "use");
                componentList.add(component);
            }
            ListDisplayer.displayList(player, "help List", componentList);

        } else {
            for (String str : helpList) {
                sender.sendMessage(Extensions.color(str));
            }
        }
    }

    public void show(CommandSender sender, String messageKey) {
        sender.sendMessage(getMessage(messageKey));
    }

    public void show(UUID uid, String messageKey) {
        Player player = Bukkit.getPlayer(uid);
        show(player, messageKey);
    }

    public void show(List<CommandSender> senders, String messageKey) {
        for (CommandSender sender : senders) {
            sender.sendMessage(getMessage(messageKey));
        }
    }

    public void save() {
        plugin.saveConfig();
    }

}
