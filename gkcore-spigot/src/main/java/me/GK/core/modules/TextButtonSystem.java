package me.GK.core.modules;

import me.GK.core.GKCore;
import me.GK.core.main.Extensions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TextButtonSystem implements CommandExecutor {
    public static TextButtonSystem instance;
    public final Integer DEFAULT_TIMEOUT = 600;
    /**
     * callbacks structure
     * {uuid:{
     * "sender": CommandSender,
     * "callback": Runnable,
     * "expires": Date
     * }}
     */
    public Map<UUID, Map<String, Object>> callbacks = new HashMap<>();

    public TextButtonSystem(JavaPlugin plugin) {
        instance = this;
        plugin.getCommand("gkcallback").setExecutor(this);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<UUID, Map<String, Object>> pair : callbacks.entrySet()) {
                if (System.currentTimeMillis() >= ((Date) pair.getValue().get("expires")).getTime()) {
                    callbacks.remove(pair.getKey());
                }
            }
        }, 0, 20);
    }

    public static BaseComponent[] generateTextButton(String str, ClickEvent clickEvent, String hoverMessage) {
        BaseComponent[] text = TextComponent.fromLegacyText(str);
        for (BaseComponent component : text) {
            component.setClickEvent(clickEvent);
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverMessage)));
        }
        return text;
    }

    public static List<BaseComponent[]> generateTextButton(List<String> strList, ClickEvent clickEvent, String hoverMessage) {
        List<BaseComponent[]> componentList = new ArrayList<>();
        for (String str : strList) {
            BaseComponent[] component = generateTextButton(str, clickEvent, hoverMessage);
            componentList.add(component);
        }
        return componentList;
    }

    public static BaseComponent[] joinComponent(BaseComponent[]... components) {
        List<BaseComponent> main = new ArrayList<>();
        for (BaseComponent[] component : components) {
            main.addAll(Arrays.asList(component));
        }
        return main.toArray(new BaseComponent[0]);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            Map<String, Object> callback = TextButtonSystem.instance.callbacks.get(UUID.fromString(args[0]));
            if (Objects.equals(((CommandSender) callback.get("sender")).getName(), sender.getName())) {
                if (callback.get("callback") == null) return true;
                ((Runnable) callback.get("callback")).run();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            GKCore.instance.messageSystem.send(sender, "invalidCallback");
        }
        return true;
    }

    public BaseComponent[] generateCallbackTextButton(CommandSender sender, Runnable callback, String text, String hoverText) {
        return generateCallbackTextButton(sender, callback, DEFAULT_TIMEOUT, text, hoverText);
    }

    public BaseComponent[] generateCallbackTextButton(CommandSender sender, Runnable callback, Integer timeout, String text, String hoverText) {
        BaseComponent[] components = TextComponent.fromLegacyText(Extensions.color(text));
        UUID uuid = UUID.randomUUID();
        if (callbacks.containsKey(uuid)) {
            // do it again because the uuid is duplicated (go buy a lottery if that happens bruh)
            return generateCallbackTextButton(sender, callback, timeout, text, hoverText);
        }
        callbacks.put(uuid, new HashMap<String, Object>() {{
            put("sender", sender);
            put("callback", callback);
            put("expires", new Date(System.currentTimeMillis() + (timeout * 1000)));
        }});
        List<BaseComponent> tempComponents = Arrays.asList(components);
        for (BaseComponent c : components) {
            c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gkcallback " + uuid));
            c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Extensions.color(hoverText))));
            components[tempComponents.indexOf(c)] = c;
        }
        return components;
    }
}
