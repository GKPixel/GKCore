package me.GK.core.containers;

import me.GK.core.main.Extensions;
import me.GK.core.GKCore;
import me.GK.core.managers.ItemStackManager;
import me.GK.core.modules.TextButtonSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
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

    public static String defaultInputTipString = GKCore.instance.configSystem.getMessage("startListeningInput");
    public UUID uid;
    public ItemStack currentEditingItemStack = new ItemStack(Material.CHEST);
    public String editorName = "";
    public String inputTipString = GKCore.instance.configSystem.getMessage("startListeningInput");
    public Runnable savingCallback = null;// the saving callback after clicking the text
    public Runnable backCallback = null;
    private ClickEvent onBack = null;// the editing callback after clicking the back sign

    public ItemStackEditor(UUID uid) {
        this.uid = uid;
    }

    private static ClickEvent getDefaultOnBack() {
        return new ClickEvent(Action.RUN_COMMAND, "/gk itemStackEditorRunBackCallback");
    }

    public static ItemStack getEditingItemStack(Player player) {
        GKPlayer GKP = GKPlayer.fromPlayer(player);
        if (GKP == null) return null;
        return GKP.itemStackEditor.currentEditingItemStack;
    }

    public static ItemStackEditor create(UUID uid, ItemStack itemStack, String editorName, String inputTipString, Runnable callback) {
        if (itemStack == null) itemStack = ItemStackManager.setDisplay(new ItemStack(Material.CHEST), "[ERROR] NULL");
        itemStack = itemStack.clone();
        GKPlayer GKP = GKPlayer.fromUUID(uid);
        if (GKP == null) return null;
        GKP.itemStackEditor.onBack = getDefaultOnBack();
        GKP.itemStackEditor.savingCallback = callback;
        GKP.itemStackEditor.editorName = editorName;
        GKP.itemStackEditor.inputTipString = inputTipString;
        GKP.itemStackEditor.currentEditingItemStack = itemStack;

        return GKP.itemStackEditor;
    }

    public static ItemStackEditor create(UUID uid, ItemStack itemStack, String editorName, Runnable callback) {
        return create(uid, itemStack, editorName, defaultInputTipString, callback);
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

    //////////////////////////////////////////////////////////////
// Editing
/////////////////////////////////////////////////////
//Send Text Button
    private void sendEditorName(Player player) {
        player.sendMessage(GKCore.instance.configSystem.getMessage("editorNameLine").replace("%name%", editorName));
    }

    private void sendBackButton(Player player) {
        TextButtonSystem.sendTextButton(player, GKCore.instance.configSystem.getMessage("backSign"), onBack,
                GKCore.instance.configSystem.getMessage("back"));
    }

    private void sendDisplayTextButton(Player player) {
        ClickEvent clickEvent = new ClickEvent(Action.RUN_COMMAND,
                "/gk itemStackEditorSetDisplay");
        TextButtonSystem.sendTextButton(player, GKCore.instance.configSystem.getMessage("display") + " : " + ChatColor.WHITE + getDisplay(),
                clickEvent, GKCore.instance.configSystem.getMessage("edit"));
    }

    private void sendLoreTextButton(Player player) {
        ClickEvent clickEvent = new ClickEvent(Action.RUN_COMMAND, "/gk itemStackEditorEditLore");
        ClickEvent copyEvent = new ClickEvent(Action.RUN_COMMAND, "/gk copyItemStackEditorEditLore");
        ClickEvent pasteEvent = new ClickEvent(Action.RUN_COMMAND, "/gk pasteItemStackEditorEditLore");
        BaseComponent[] b1 = TextButtonSystem.generateTextButton(player, GKCore.instance.configSystem.getMessage("lore") + " : ", clickEvent, GKCore.instance.configSystem.getMessage("edit"));
        BaseComponent[] copy = TextButtonSystem.generateTextButton(player, GKCore.instance.configSystem.getMessage("copy") + " ", copyEvent, GKCore.instance.configSystem.getMessage("copy"));
        BaseComponent[] paste = TextButtonSystem.generateTextButton(player, GKCore.instance.configSystem.getMessage("paste") + " ", pasteEvent, GKCore.instance.configSystem.getMessage("paste"));
        BaseComponent[] loreLine = TextButtonSystem.joinComponent(TextButtonSystem.joinComponent(b1, copy), paste);
        player.spigot().sendMessage(loreLine);
        //TextButtonSystem.sendTextButton(player, GKCore.instance.configSystem.getMessage("lore") + " : ", clickEvent,GKCore.instance.configSystem.getMessage("edit"));
        for (String line : getLore()) {
            TextButtonSystem.sendTextButton(player, GKCore.instance.configSystem.getMessage("lorePin") + line, clickEvent,
                    GKCore.instance.configSystem.getMessage("edit"));

        }
    }

    private void sendChangeItemTextButton(Player player) {
        ClickEvent clickEvent = new ClickEvent(Action.RUN_COMMAND, "/gk itemStackEditorSetItemInHand");
        TextButtonSystem.sendTextButton(player, GKCore.instance.configSystem.getMessage("changeItemStack"), clickEvent,
                GKCore.instance.configSystem.getMessage("clickToChangeItemStack"));

    }

    private void sendGetItemTextButton(Player player) {
        ClickEvent clickEvent = new ClickEvent(Action.RUN_COMMAND, "/gk itemStackEditorGetItem");
        TextButtonSystem.sendTextButton(player, GKCore.instance.configSystem.getMessage("clickToGetItemStack"), clickEvent,
                GKCore.instance.configSystem.getMessage("clickToGetItemStack"));

    }

    /////////////////////////////////////////////////////
//Events
    public ItemStackEditor onBack(ClickEvent event) {
        this.onBack = event;
        return this;
    }

    public ItemStackEditor onBack(Runnable runnable) {
        this.backCallback = runnable;
        this.onBack = getDefaultOnBack();
        return this;
    }

    /////////////////////////////////////////////////////
//Editing with Text Button
    public void send() {
        edit(getPlayer());
    }

    public void edit(Player player) {
        GKCore.instance.configSystem.show(player, "topSplitter");
        sendEditorName(player);
        sendBackButton(player);
        sendDisplayTextButton(player);
        sendLoreTextButton(player);
        sendChangeItemTextButton(player);
        sendGetItemTextButton(player);
        GKCore.instance.configSystem.show(player, "bottomSplitter");
    }

    public void editLore() {
        Player player = getPlayer();
        List<String> lore = getLore();
        ListEditor.create(player, lore, GKCore.instance.configSystem.getMessage("loreEditor"), new Runnable() {

            @Override
            public void run() {
                List<String> newLore = ListEditor.getEditingList(player);
                setLore(newLore);
                savingCallback.run();
            }

        })
                .onBack(new ClickEvent(Action.RUN_COMMAND, "/gk itemStackEditorEdit"))
                .send();
	}

}