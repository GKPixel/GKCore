package me.GK.core.main;

import me.GK.core.GKCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Extensions extends Plugin {
    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void runServerCommand(String cmd) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), cmd);
    }

    public static int random(int min, int max) {
        return (int) (Math.floor(min + (Math.random() * (max - min))));
    }

    public static float random(float min, float max) {
        float gap = max - min;
        float add = (float) (gap * Math.random());
        return min + add;
    }

    public static long stringToMillis(String str) {
        Duration d = Duration.parse(str);
        return d.toMillis();
    }

    public static String millisToString(long millis, String lang) {
        Duration d = Duration.ofMillis(millis);
        int days = (int) d.toDays();
        d = d.minusDays(days);
        int hours = (int) d.toHours();
        d = d.minusHours(hours);
        int minutes = (int) d.toMinutes();
        return String.format("%d %s %d %s %d %s", days, GKCore.instance.messageSystem.get(lang, "days"),
                hours, GKCore.instance.messageSystem.get(lang, "hours"),
                minutes, GKCore.instance.messageSystem.get(lang, "minutes"));
    }

    public static List<String> mapToList(LinkedHashMap<String, String> map) {
        List<String> result = new ArrayList<>();
        for (String key : map.keySet()) {
            String value = map.get(key);
            result.add(key + ": " + value);
        }
        return result;
    }

    public static LinkedHashMap<String, String> listToMap(List<String> list) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (String line : list) {
            String[] splitted = line.split(": ");
            if (splitted.length < 2) {
                result.put(line.trim(), null);
            } else {
                result.put(splitted[0].trim(), splitted[1].trim());
            }
        }
        return result;
    }

    public static boolean intToBoolean(int i) {
        if (i == 0) {
            return false;
        } else return i == 1;
    }
}
