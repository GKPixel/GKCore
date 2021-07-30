package me.GK.core.managers;

import me.GK.core.GKCore;
import me.GK.core.containers.ListEditor;
import me.GK.core.main.Extensions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackManager {
    ////////////////////////////////////////////////////////
    //Item Display
    public static String getDisplay(ItemStack item) {
        if (item == null) return "";
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item.getType().name();
        }
        if (meta.getDisplayName() == null) {
            return item.getType().name();
        }
        if (meta.getDisplayName().equals("")) {
            return item.getType().name();
        }
        return meta.getDisplayName();
    }

    public static ItemStack setDisplay(ItemStack item, String displayName) {
        if (item == null) return item;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }
    ////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////
    //Lore
    public static ItemStack createItem(String name, ArrayList<String> desc, Material mat) {
        ItemStack i = new ItemStack(mat, 1);
        ItemMeta iMeta = i.getItemMeta();
        iMeta.setDisplayName(name);
        iMeta.setLore(desc);
        i.setItemMeta(iMeta);
        return i;
    }

    public static List<String> getLore(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return new ArrayList<String>();
        List<String> lore = meta.getLore();
        if (lore == null) return new ArrayList<String>();
        return lore;
    }

    public static ItemStack addLore(ItemStack stack, String str) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<String>();
        lore.add(str);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack removeLoreLine(ItemStack stack, int line) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;
        List<String> lore = meta.getLore();
        if (lore == null) return stack;
        lore.remove(line);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack removeLoreLastLine(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;
        List<String> lore = meta.getLore();
        if (lore == null) return stack;
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack setLore(ItemStack stack, List<String> lore) {
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            lore.set(i, ChatColor.WHITE + line);
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public static void editLore(Player player, ItemStack stack, Runnable savingCallback) {
        List<String> lore = getLore(stack);
        ListEditor.create(player, lore, Extensions.color(GKCore.instance.messageSystem.get("loreEditor")), savingCallback).send();
    }
    ////////////////////////////////////////////////////////

}
