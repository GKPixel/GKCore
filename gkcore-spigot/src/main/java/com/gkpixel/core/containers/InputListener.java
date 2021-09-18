package com.gkpixel.core.containers;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.modules.TextButtonSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
public class InputListener {
    public static String defaultInputTipString = GKCore.instance.messageSystem.get("startListeningInput");
    public UUID uid;
    public boolean listeningInput = false;
    public String inputTipString = "";
    public Runnable callback;
    public String latestInput = "";

    public InputListener(UUID uid) {
        this.uid = uid;
    }

    public static InputListener create(UUID uid, String currentString, String inputTipString, Runnable callback) {
        GKPlayer GKP = GKPlayer.fromUUID(uid);
        if (GKP == null) return null;
        GKP.inputListener.latestInput = currentString;
        GKP.inputListener.callback = callback;
        GKP.inputListener.inputTipString = inputTipString;

        return GKP.inputListener;
    }

    public static InputListener create(UUID uid, String inputTipString, Runnable callback) {
        return create(uid, "", inputTipString, callback);
    }

    public static InputListener create(UUID uid, Runnable callback) {
        return create(uid, defaultInputTipString, callback);
    }

    public static String getInput(Player player) {
        GKPlayer GKP = GKPlayer.fromPlayer(player);
        if (GKP == null) return null;
        return GKP.inputListener.latestInput;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    @SuppressWarnings("unused")
    private void debug(String str) {
        ////System.out.print(str);
    }

    ////////////////////////////////////////////////////////////
    //Commands callback
    public void send() {
        startListening();
    }

    public void startListening() {
        getPlayer().sendMessage(inputTipString);

        listeningInput = true;
        //Cancel Button
        String cancelString = GKCore.instance.messageSystem.get("cancelButton");
        ClickEvent cancelEvent = new ClickEvent(Action.RUN_COMMAND, "cancel");
        String cancelHover = GKCore.instance.messageSystem.get("cancelHover");
        BaseComponent[] resultLine = TextButtonSystem.generateTextButton(cancelString, cancelEvent, cancelHover);

        if (latestInput.length() > 0) {//Clonable
            //Clone Button
            String lastInput = this.latestInput.replace(ChatColor.COLOR_CHAR, '&');
            while (lastInput.startsWith("&f")) {
                lastInput = lastInput.substring(2);
            }
            String cloneString = GKCore.instance.messageSystem.get("cloneButton");
            ClickEvent cloneEvent = new ClickEvent(Action.SUGGEST_COMMAND, lastInput);
            String cloneHover = GKCore.instance.messageSystem.get("cloneHover");
            BaseComponent[] cloneButton = TextButtonSystem.generateTextButton(cloneString, cloneEvent, cloneHover);
            resultLine = TextButtonSystem.joinComponent(resultLine, cloneButton);
        }
        getPlayer().spigot().sendMessage(resultLine);
    }

    public boolean checkInput(String input) {
        if (ChatColor.stripColor(input.toLowerCase()).equals("cancel")) {
            GKCore.instance.messageSystem.send(getPlayer(), "cancelledSuccessfully");
            listeningInput = false;
            return true;
        }
        if (listeningInput) {
            latestInput = input;
            callback.run();
            listeningInput = false;
            return true;
        }
        return false;

    }
}