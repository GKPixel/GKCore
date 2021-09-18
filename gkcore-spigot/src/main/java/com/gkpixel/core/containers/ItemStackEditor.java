package com.gkpixel.core.containers;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.main.Extensions;
import com.gkpixel.core.managers.ItemStackManager;
import com.gkpixel.core.modules.TextButtonSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Parse {@link ItemStack} to JSON
 *
 * @author DevSrSouza
 * @version 1.0
 * <p>
 * https://github.com/DevSrSouza/ You can find updates here
 * https://gist.github.com/DevSrSouza
 */
public class ItemStackEditor {
    public UUID uid;
    public ItemStack currentEditingItemStack = new ItemStack(Material.CHEST);
    public String editorName = "";
    public Runnable savingCallback = null;// the saving callback after clicking the text
    public Runnable backCallback = null;

    public ItemStackEditor(UUID uid) {
        this.uid = uid;
    }

    public static ItemStack getEditingItemStack(Player player) {
        GKPlayer GKP = GKPlayer.fromPlayer(player);
        if (GKP == null) return null;
        return GKP.itemStackEditor.currentEditingItemStack;
    }

    public static ItemStackEditor create(UUID uid, ItemStack itemStack, String editorName, Runnable callback) {
        if (itemStack == null) itemStack = ItemStackManager.setDisplay(new ItemStack(Material.CHEST), "[ERROR] NULL");
        itemStack = itemStack.clone();
        GKPlayer GKP = GKPlayer.fromUUID(uid);
        if (GKP == null) return null;
        GKP.itemStackEditor.savingCallback = callback;
        GKP.itemStackEditor.editorName = editorName;
        GKP.itemStackEditor.currentEditingItemStack = itemStack;

        return GKP.itemStackEditor;
    }

    public static ItemStackEditor create(Player player, ItemStack itemStack, String editorName, Runnable callback) {
        UUID uid = player.getUniqueId();
        return create(uid, itemStack, editorName, callback);
    }

    public void save() {
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    private void debug(String str) {
        //// System.out.print(str);
    }

    public String getDisplay() {
        return ItemStackManager.getDisplay(currentEditingItemStack);
    }

    public void setDisplay(String input) {
        currentEditingItemStack = ItemStackManager.setDisplay(currentEditingItemStack, Extensions.color(input));

        savingCallback.run();
        send();
    }

    public List<String> getLore() {
        return ItemStackManager.getLore(currentEditingItemStack);
    }

    public void setLore(List<String> input) {
        currentEditingItemStack = ItemStackManager.setLore(currentEditingItemStack, input);
        savingCallback.run();
        send();
    }

    public ItemStack getItemStack() {
        return currentEditingItemStack;
    }

    public void giveItemStack(Player player) {
        player.getInventory().addItem(getItemStack());
    }

    public void setItem(ItemStack itemStack) {
        currentEditingItemStack = itemStack.clone();
        savingCallback.run();
        send();
    }

    private void sendEditorName(Player player) {
        player.sendMessage(Extensions.color(GKCore.instance.messageSystem.get(player, "editorNameLine")).replace("%name%", editorName));
    }

    private void sendBackButton(Player player) {
        player.spigot().sendMessage(TextButtonSystem.instance.generateCallbackTextButton(player, this.backCallback, GKCore.instance.messageSystem.get(player, "backSign"),
                GKCore.instance.messageSystem.get(player, "back")));
    }

    private void sendDisplayTextButton(Player player) {
        player.spigot().sendMessage(TextButtonSystem.instance.generateCallbackTextButton(player, () ->
                        InputListener.create(uid, getDisplay(), GKCore.instance.messageSystem.get(player, "startListeningInput"), () -> {
                            String input = InputListener.getInput(player);
                            setDisplay(input);
                        }).send(), GKCore.instance.messageSystem.get(player, "display") + " : " + ChatColor.WHITE + getDisplay(),
                GKCore.instance.messageSystem.get(player, "edit")));
    }

    private void sendLoreTextButton(Player player) {
        BaseComponent[] b1 = TextButtonSystem.instance.generateCallbackTextButton(player, this::editLore, GKCore.instance.messageSystem.get(player, "lore") + " : ", GKCore.instance.messageSystem.get(player, "edit"));
        BaseComponent[] copy = TextButtonSystem.instance.generateCallbackTextButton(player, () -> {
            GKPlayer GKP = GKPlayer.fromUUID(uid);
            GKP.listEditor.clipboard = GKP.itemStackEditor.getLore();
            player.sendMessage(GKCore.instance.messageSystem.get(player, "copiedLore"));
        }, GKCore.instance.messageSystem.get(player, "copy") + " ", GKCore.instance.messageSystem.get(player, "copy"));
        BaseComponent[] paste = TextButtonSystem.instance.generateCallbackTextButton(player, () -> {
            GKPlayer GKP = GKPlayer.fromUUID(uid);
            GKP.itemStackEditor.setLore(GKP.listEditor.clipboard);
        }, GKCore.instance.messageSystem.get(player, "paste") + " ", GKCore.instance.messageSystem.get(player, "paste"));

        BaseComponent[] loreLine = TextButtonSystem.joinComponent(b1, copy, paste);
        player.spigot().sendMessage(loreLine);

        for (String line : getLore()) {
            TextButtonSystem.instance.generateCallbackTextButton(player, this::editLore, GKCore.instance.messageSystem.get(player, "lorePin") + line,
                    GKCore.instance.messageSystem.get(player, "edit"));

        }
    }

    private void sendChangeItemTextButton(Player player) {
        player.spigot().sendMessage(TextButtonSystem.instance.generateCallbackTextButton(player, () -> {
                    GKPlayer GKP = GKPlayer.fromUUID(uid);
                    if (player.getInventory() == null ||
                            player.getInventory().getItemInMainHand() == null ||
                            player.getInventory().getItemInMainHand().getType() == Material.AIR)
                        return;
                    GKP.itemStackEditor.setItem(player.getInventory().getItemInMainHand());
                }, GKCore.instance.messageSystem.get(player, "changeItemStack"),
                GKCore.instance.messageSystem.get(player, "clickToChangeItemStack")));
    }

    private void sendGetItemTextButton(Player player) {
        TextButtonSystem.instance.generateCallbackTextButton(player, () -> {
                    giveItemStack(player);
                    GKCore.instance.messageSystem.send(player, "commands.done");
                }, GKCore.instance.messageSystem.get(player, "clickToGetItemStack"),
                GKCore.instance.messageSystem.get(player, "clickToGetItemStack"));
    }

    public ItemStackEditor onBack(Runnable runnable) {
        this.backCallback = runnable;
        return this;
    }

    /////////////////////////////////////////////////////
//Editing with Text Button
    public void send() {
        edit(getPlayer());
    }

    public void edit(Player player) {
        sendEditorName(player);
        sendBackButton(player);
        sendDisplayTextButton(player);
        sendLoreTextButton(player);
        sendChangeItemTextButton(player);
        sendGetItemTextButton(player);
        GKCore.instance.messageSystem.send(player, "bottomSplitter");
    }

    public void editLore() {
        Player player = getPlayer();
        List<String> lore = getLore();
        ListEditor.create(player, lore, Extensions.color(GKCore.instance.messageSystem.get(player, "loreEditor")), () -> {
            List<String> newLore = ListEditor.getEditingList(player);
            setLore(newLore);
            savingCallback.run();
        }).onBack(() -> edit(player)).send();
    }

}