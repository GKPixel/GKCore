package me.GK.core.modules;

import me.GK.core.GKCore;
import me.GK.core.main.Extensions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TextButtonSystem extends JavaPlugin {
    public static String color(String str) {
        return Extensions.color(str);
    }

    //////////////////////////////////////////////////////
    //Sending
    public static void sendTextButton(Player player, String str, ClickEvent clickEvent, String hoverMessage) {
        BaseComponent[] text = TextComponent.fromLegacyText(str);
        for (BaseComponent component : text) {
            component.setClickEvent(clickEvent);
            HoverEvent hoverEvent = generateHoverEvent(color(hoverMessage));
            component.setHoverEvent(hoverEvent);
        }
        player.spigot().sendMessage(text);
    }

    public static void sendTextButton(Player player, List<String> strList, ClickEvent clickEvent, String hoverMessage) {
        for (String str : strList) {
            sendTextButton(player, str, clickEvent, hoverMessage);
        }
    }

    public static void sendTextButton(UUID uid, String str, ClickEvent clickEvent, String hoverMessage) {
        Player player = Bukkit.getPlayer(uid);
        sendTextButton(player, str, clickEvent, hoverMessage);
    }

    public static void sendTextButton(UUID uid, List<String> strList, ClickEvent clickEvent, String hoverMessage) {
        Player player = Bukkit.getPlayer(uid);
        sendTextButton(player, strList, clickEvent, hoverMessage);
    }
    //////////////////////////////////////////////////////


    //////////////////////////////////////////////////////
    //Generating Text Button
    public static BaseComponent[] generateTextButton(Player player, String str, ClickEvent clickEvent, String hoverMessage) {
        BaseComponent[] text = TextComponent.fromLegacyText(str);
        for (BaseComponent component : text) {
            component.setClickEvent(clickEvent);
            HoverEvent hoverEvent = generateHoverEvent(color(hoverMessage));
            component.setHoverEvent(hoverEvent);
        }
        return text;
    }

    public static List<BaseComponent[]> generateTextButton(Player player, List<String> strList, ClickEvent clickEvent, String hoverMessage) {
        List<BaseComponent[]> componentList = new ArrayList<BaseComponent[]>();
        for (String str : strList) {
            BaseComponent[] component = generateTextButton(player, str, clickEvent, hoverMessage);
            componentList.add(component);
        }
        return componentList;
    }

    public static BaseComponent[] generateTextButton(UUID uid, String str, ClickEvent clickEvent, String hoverMessage) {
        Player player = Bukkit.getPlayer(uid);
        return generateTextButton(player, str, clickEvent, hoverMessage);
    }

    public static List<BaseComponent[]> generateTextButton(UUID uid, List<String> strList, ClickEvent clickEvent, String hoverMessage) {
        Player player = Bukkit.getPlayer(uid);
        return generateTextButton(player, strList, clickEvent, hoverMessage);
    }

    //Custom Button
    public static BaseComponent[] generateAddButton(Player player, ClickEvent clickEvent, String hoverMessage) {
        String str = GKCore.instance.messageSystem.get("addSign");
        return generateTextButton(player, str, clickEvent, hoverMessage);
    }

    public static BaseComponent[] generateRemoveButton(Player player, ClickEvent clickEvent, String hoverMessage) {
        String str = GKCore.instance.messageSystem.get("removeSign");
        return generateTextButton(player, str, clickEvent, hoverMessage);
    }

    public static BaseComponent[] generateBackButton(Player player, ClickEvent clickEvent, String hoverMessage) {
        String str = GKCore.instance.messageSystem.get("backSign");
        return generateTextButton(player, str, clickEvent, hoverMessage);
    }

    //////////////////////////////////////////////////////
    //Generation

    public static HoverEvent generateHoverEvent(String str) {
        return new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(str));
    }

    //////////////////////////////////////////////////////
    //Other
    public static BaseComponent[] joinComponent(BaseComponent[] a, BaseComponent[] b) {
        List<BaseComponent> list = Arrays.asList(a);
        List<BaseComponent> arrList = new ArrayList<BaseComponent>(list);
        for (BaseComponent component : b) {
            arrList.add(component);
        }
        BaseComponent[] array = new BaseComponent[arrList.size()];
        array = arrList.toArray(array);
        return array;
    }
}
