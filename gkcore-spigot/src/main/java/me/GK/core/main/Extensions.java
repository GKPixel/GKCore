package me.GK.core.main;

import me.GK.core.GKCore;
import me.GK.core.modules.MessagePackage.ActionBar;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Extensions extends JavaPlugin {
    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }


    public static void sendCommandButton(String targetPlayerName, String text, String command) {
        text = GKCore.instance.messageSystem.get("systemName") + text;
        String c = "tellraw " + targetPlayerName + " [\"\",{\"text\":\"" + text + "\",\"italic\":true,\"underlined\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}}]";
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), c);
    }

    /////////////////////////////////////////////////
    //Action Bar
    public static void sendActionBar(Player player, String str, int duration) {
        if (player.isOnline()) {
            ActionBar actionbar = new ActionBar(str);
            actionbar.send(player);
        }
    }

    public static void sendActionBar(Player player, String str) {
        sendActionBar(player, str, 1);
    }
    /////////////////////////////////////////////////

    /////////////////////////////////////////////////
    //Command
    public static void runCommandForPlayer(Player player, String cmd) {
        player.performCommand(cmd);
    }

    public static void runCommandForPlayerList(Player player, List<String> cmdList) {
        for (String cmd : cmdList) {
            runCommandForPlayer(player, cmd);
        }

    }

    public static void runServerCommand(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
    }
    /////////////////////////////////////////////////


    ////////////////////////////////////////////////////////
    //Title Message
	/*
	public static void titleMessageAll(String title, String subtitle) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle);
		}
	}
	@SuppressWarnings("deprecation")
	public static void titleMessageAll(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle);
		}
	}
	*/
    ////////////////////////////////////////////////////////

    public static float random(float min, float max) {
        float gap = max - min;
        float add = (float) (gap * Math.random());
        float value = min + add;
        return value;
    }

    ////////////////////////////////////////////////////////
    //Duration`
    public static long stringToMillis(String str) {
        Duration d = Duration.parse(str);
        return d.toMillis();
    }
    ////////////////////////////////////////////////////////

    public static String millisToString(long millis) {
        Duration d = Duration.ofMillis(millis);
        int days = (int) d.toDays();
        d = d.minusDays(days);
        int hours = (int) d.toHours();
        d = d.minusHours(hours);
        int minutes = (int) d.toMinutes();
        d = d.minusMinutes(minutes);
        String str = days + " days " + hours + " hours " + minutes + " minutes ";
        return str;
    }

    public static String millisToStringTranslated(long millis) {
        String durationString = millisToString(millis);
        durationString = durationString
                .replace("days", GKCore.instance.messageSystem.get("days"))
                .replace("hours", GKCore.instance.messageSystem.get("hours"))
                .replace("minutes", GKCore.instance.messageSystem.get("minutes"))
                .replace("seconds", GKCore.instance.messageSystem.get("seconds"));
        return durationString;
    }

    ////////////////////////////////////////////////////////
    //List and Map
    public static List<String> mapToList(LinkedHashMap<String, String> map) {
        List<String> result = new ArrayList<String>();
        for (String key : map.keySet()) {
            String value = map.get(key);
            result.add(key + ": " + value);
        }
        return result;
    }

    public static LinkedHashMap<String, String> listToMap(List<String> list) {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
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

    ////////////////////////////////////////////////////////
    //Misc
    public static boolean intToBoolean(int i) {
        if (i == 0) {
            return false;
        } else return i == 1;
    }

    public static void teleportToLobby(Player player) {
        runServerCommand("mvtp " + player.getName() + " " + GKCore.instance.configSystem.get("lobbyName"));
    }

    /*
        public static String timeConvert(long minutes) {
            String result = "";
            int displayDays = (int)minutes/24/60;
            int displayHours = (int)minutes/60%24;
            int displayMinutes = (int)minutes%60;
            if(displayDays > 0) {
                result+=displayDays+GKCore.instance.messageSystem.get("day");
            }
            if(displayHours > 0) {
                result+=displayHours+GKCore.instance.messageSystem.get("hour");
            }
            if(displayMinutes > 0) {
                result+=displayMinutes+GKCore.instance.messageSystem.get("minute");
            }
            return result;
        }*/
    public static int getOppositeTeam(int teamID) {
        if (teamID == 1) {
            return 0;
        } else {
            return 1;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getConfigSectionValue(Object o, boolean deep) {
        if (o == null) {
            return null;
        }
        Map<String, Object> map;
        if (o instanceof ConfigurationSection) {
            map = ((ConfigurationSection) o).getValues(deep);
        } else if (o instanceof Map) {
            map = (Map<String, Object>) o;
        } else {
            return null;
        }
        return map;
    }

    public static Object readMap(ConfigurationSection section, String sectionPath) {
        return getConfigSectionValue(section.getConfigurationSection(sectionPath), true);
    }

    ////////////////////////////////////////////////////////
    //Random
    public static int irandom(int min, int max) {
        // max++;
        return (int) (Math.floor(min + (Math.random() * (max - min))));
    }
    ////////////////////////////////////////////////////////
    public static long getCurrentUnixTime(){
        return System.currentTimeMillis() / 1000L;
    }
}
