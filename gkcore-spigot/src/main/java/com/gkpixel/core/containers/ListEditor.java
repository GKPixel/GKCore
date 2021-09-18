package com.gkpixel.core.containers;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.main.Extensions;
import com.gkpixel.core.modules.TextButtonSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Parse {@link ItemStack} to JSON
 *
 * @author DevSrSouza
 * @version 1.0
 * <p>
 * https://github.com/DevSrSouza/
 * You can find updates here https://gist.github.com/DevSrSouza
 */
public class ListEditor {
    private static final String upSign = "&6&l[▲]";
    private static final String downSign = "&6&l[▼]";
    private static final String addSign = "&a&l[+]";
    private static final String removeSign = "  &c&l[X]";
    private static final String backSign = "&c&l[<--]";
    public static String defaultInputTipString = GKCore.instance.messageSystem.get("startListeningInput");
    public UUID uid;
    public List<String> clipboard = new ArrayList<String>();
    public List<String> currentEditingList = new ArrayList<String>();
    public String editorName = "";
    public String inputTipString = GKCore.instance.messageSystem.get("startListeningInput");
    public Runnable savingCallback = null;
    public Runnable backCallback = null;
    public ClickEvent backClickEvent = null;
    public int editingLine = -1;

    public ListEditor(UUID uid) {
        this.uid = uid;
    }

    public static void initiate() {
    }

    public static ListEditor create(UUID uid, List<String> list, String editorName, String inputTipString, Runnable callback) {
        if (list == null) list = new ArrayList<>();
        GKPlayer GKP = GKPlayer.fromUUID(uid);
        if (GKP == null) return null;
        GKP.listEditor.resetEvent();
        GKP.listEditor.savingCallback = callback;
        GKP.listEditor.editorName = editorName;
        GKP.listEditor.inputTipString = inputTipString;
        GKP.listEditor.currentEditingList = new ArrayList<>(list);

        return GKP.listEditor;
    }

    public static ListEditor create(UUID uid, List<String> list, String editorName, Runnable callback) {
        return create(uid, list, editorName, defaultInputTipString, callback);
    }

    public static ListEditor create(Player player, List<String> list, String editorName, Runnable callback) {
        UUID uid = player.getUniqueId();
        return create(uid, list, editorName, callback);
    }

    public static ListEditor create(UUID uid, LinkedHashMap<String, String> map, String editorName, String inputTipString, Runnable callback) {
        List<String> list = Extensions.mapToList(map);
        return create(uid, list, editorName, inputTipString, callback);

    }

    public static ListEditor create(UUID uid, LinkedHashMap<String, String> map, String editorName, Runnable callback) {
        return create(uid, map, editorName, defaultInputTipString, callback);
    }

    public static ListEditor create(Player player, LinkedHashMap<String, String> map, String editorName, Runnable callback) {
        return create(player.getUniqueId(), map, editorName, callback);
    }

    public static List<String> getEditingList(Player player) {
        GKPlayer GKP = GKPlayer.fromPlayer(player);
        if (GKP == null) return null;
        return GKP.listEditor.currentEditingList;
    }

    ///////////////////////////////////////////////////////////

    public static LinkedHashMap<String, String> getEditingMap(Player player) {
        return Extensions.listToMap(getEditingList(player));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    private void debug(String str) {
        ////System.out.print(str);
    }

    public ListEditor setTipString(String str) {
        this.inputTipString = str;
        return this;
    }

    ////////////////////////////////////////////////////////////
    //Commands callback
    public void startListeningEditInput(int editingLine) {
        String currentString = "";
        if (currentEditingList.size() > editingLine) {
            currentString = currentEditingList.get(editingLine);
            getPlayer().sendMessage("currentString: " + currentString);
        }
        InputListener.create(uid, currentString, inputTipString, () -> {
                    final GKPlayer GKP = GKPlayer.fromUUID(uid);
                    if (editingLine == currentEditingList.size()) currentEditingList.add("");
                    currentEditingList.set(editingLine, GKP.inputListener.latestInput);
                    //save current list
                    savingCallback.run();
                    send();
                })
                .send();
        this.editingLine = editingLine;
    }

    public void listGoUp(int editingLine) {
        debug("received go up command");
        if (editingLine - 1 >= 0) {
            String current = currentEditingList.get(editingLine);
            String target = currentEditingList.get(editingLine - 1);
            currentEditingList.set(editingLine, target);
            currentEditingList.set(editingLine - 1, current);
        }
        savingCallback.run();
        send();
    }
    ///////////////////////////////////////////////////////////

    public void listGoDown(int editingLine) {
        debug("received go down command");
        int length = currentEditingList.size();
        if (editingLine + 1 < length) {
            String current = currentEditingList.get(editingLine);
            String target = currentEditingList.get(editingLine + 1);
            currentEditingList.set(editingLine, target);
            currentEditingList.set(editingLine + 1, current);
        }
        savingCallback.run();
        send();
    }

    public void removeLine(int editingLine) {
        debug("received go down command");
        int length = currentEditingList.size();
        if (editingLine < length) {
            currentEditingList.remove(editingLine);
        }
        savingCallback.run();
        send();
    }

    private void setHoverDescription(BaseComponent component, String str, ChatColor color) {
        ArrayList<BaseComponent> componentList = new ArrayList<BaseComponent>();
        BaseComponent description = new TextComponent(str);
        description.setColor(color);
        componentList.add(description);
        BaseComponent[] baseComponentArray = new BaseComponent[componentList.size()];
        for (int i = 0; i < componentList.size(); i++) {
            baseComponentArray[i] = componentList.get(i);
        }
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, baseComponentArray));

    }

    ///////////////////////////////////////////////////////////
    //get Components
    private BaseComponent[] getRemoveSignComponent(Player player, int lineID) {
        return TextButtonSystem.instance.generateCallbackTextButton(player, () -> removeLine(lineID), removeSign, "&c&lRemove");
    }

    private BaseComponent[] getBackSignComponent(Player player) {
        if (this.backCallback == null) {
            return TextButtonSystem.generateTextButton(Extensions.color(backSign), this.backClickEvent, "&cBack");
        } else {
            return TextButtonSystem.instance.generateCallbackTextButton(player, this.backCallback, Extensions.color(backSign), "&cBack");
        }
    }

    private BaseComponent[] getAddSignComponent(Player player, int lineID) {
        return TextButtonSystem.instance.generateCallbackTextButton(player, () -> startListeningEditInput(lineID), addSign, "&aAdd");
    }

    private BaseComponent[] getEditLineComponent(Player player, int lineID, String str) {
        BaseComponent[] textLine = TextComponent.fromLegacyText(str);
        BaseComponent[] result = {};
        for (BaseComponent text : textLine) {
            result = (BaseComponent[]) ArrayUtils.addAll(result, TextButtonSystem.instance.generateCallbackTextButton(player, () -> startListeningEditInput(lineID), text.toLegacyText(), "&eEdit"));
        }
        return (BaseComponent[]) ArrayUtils.addAll(result, getRemoveSignComponent(player, lineID));
    }
    /////////////////////////////////////////////////////////////////////////

    private BaseComponent[] getUpTextComponent(Player player, int lineID) {
        return TextButtonSystem.instance.generateCallbackTextButton(player, () -> listGoUp(lineID), upSign, "&eMove Up");
    }

    private BaseComponent[] getDownTextComponent(Player player, int lineID) {
        return TextButtonSystem.instance.generateCallbackTextButton(player, () -> listGoDown(lineID), downSign, "&eMove Down");
    }

    ///////////////////////////////////////////////////////////
    //Sending
    private BaseComponent[] getLine(Player player, int lineID, String str) {
        debug("got line");
        TextComponent text = new TextComponent("");
        for (BaseComponent component : getUpTextComponent(player, lineID)) {
            text.addExtra(component);
        }
        for (BaseComponent component : getDownTextComponent(player, lineID)) {
            text.addExtra(component);
        }
        text.addExtra(" ");
        for (BaseComponent component : getEditLineComponent(player, lineID, str)) {
            text.addExtra(component);
        }

        return getBaseComponent(text);
    }

    private BaseComponent[] getBaseComponent(BaseComponent b) {
        BaseComponent[] result = new BaseComponent[1];
        result[0] = b;
        return result;
    }

    public void sendEditor(Player player) {
        debug("sent editor");
        List<BaseComponent[]> componentList = new ArrayList<BaseComponent[]>();
        componentList.add(getBackSignComponent(player));
        for (int lineID = 0; lineID < currentEditingList.size(); lineID++) {
            String lineString = currentEditingList.get(lineID);
            BaseComponent[] component = getLine(player, lineID, lineString);
            componentList.add(component);
        }
        componentList.add(getAddSignComponent(player, currentEditingList.size()));
        ListDisplayer.displayList(player, editorName, componentList);
    }

    public void send() {
        sendEditor(getPlayer());
    }

    private void resetEvent() {
        this.backClickEvent = null;
        this.backCallback = null;
    }

    public ListEditor onBack(Runnable event) {
        this.backCallback = event;
        return this;
    }

    public ListEditor onBack(ClickEvent event) {
        this.backClickEvent = event;
        return this;
    }
}