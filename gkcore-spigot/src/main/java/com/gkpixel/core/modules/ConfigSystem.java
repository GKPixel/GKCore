package com.gkpixel.core.modules;

import com.gkpixel.core.gui.ButtonEditor;
import com.gkpixel.core.managers.ItemStackManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigSystem {
    public FileConfiguration config;
    public HashMap<String, ItemStack> buttons = new HashMap<>();
    public List<String> helpList = new ArrayList<>();
    Plugin plugin;

    public ConfigSystem(Plugin plugin) {
        this.plugin = plugin;
        initiate();
    }

    public void initiate() {
        plugin.saveDefaultConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        reload();

    }

    private ConfigurationSection getSectionSafely(ConfigurationSection currentSection, String sectionName) {
        if (currentSection.contains(sectionName)) return currentSection.getConfigurationSection(sectionName);
        return currentSection.createSection(sectionName);
    }

    private void loadAllButtons() {
        buttons.clear();
        ConfigurationSection buttonConfig = getSectionSafely(config, "Buttons");
        for (String key : buttonConfig.getKeys(false)) {
            ItemStack button = buttonConfig.getItemStack(key);
            buttons.put(key, button);
        }
    }


    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadAllButtons();
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

    public void save() {
        plugin.saveConfig();
    }

}
