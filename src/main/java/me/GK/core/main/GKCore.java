package me.GK.core.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import gk.minuskube.inv.InventoryManager;
import me.GK.core.containers.ListEditor;
import me.GK.core.managers.GKPlayerManager;
import me.GK.core.managers.ItemStackManager;
import me.GK.core.modules.ConfigSystem;
import me.GK.core.modules.JsonSystem;
import me.GK.core.modules.Version;
import me.clip.placeholderapi.PlaceholderAPI;
import me.GK.core.mysql.MYSQLConfig;
import me.GK.core.mysql.MySQL;


public class GKCore extends JavaPlugin{
	public static Plugin plugin;
	public static GKCore instance;
	
	public JsonSystem jsonSystem = JsonSystem.create();
	public ConfigSystem configSystem;
    public InventoryManager invManager;
    
	public static GKCore getInstance() {
		return instance;
	}
	///////////////////////////////////////////////////////
	//Debug
	public static boolean debugging = true;
	public static void startDebugging() {
		debugging = true;
	}
	public static void endDebugging() {
		debugging = false;
	}
	private static int maxDebugPerFrame = 10;
	private static List<String> tempDebugList = new ArrayList<String>();
    private static ConsoleCommandSender consoleSender;
	public static void debug(Object obj) {
		if(debugging)
			tempDebugList.add(obj.toString());//add into the temp list and wait for the queue
	}
	public static Player G7 = Bukkit.getPlayer("hiIamG7");
	public static void G7_log(Object msg) {
		if(G7!=null) G7.sendMessage(msg.toString());
	}

	public static void forceDebug(Object obj) {
		tempDebugList.add(obj.toString());//add into the temp list and wait for the queue
	}
	public static void debugError(Object obj) {
		forceDebug(obj);
	}
	private static void sendOldestDebug() {//send the oldest
		if(tempDebugList.size() > 0) {
			String debugStr = tempDebugList.get(0);
			consoleSender.sendMessage(debugStr);
			tempDebugList.remove(0);
		}
	}
	public void updateDebugModeFromConfig() {
		GKCore.debugging = Boolean.parseBoolean(GKCore.instance.configSystem.get("debugMode"));
	}
	@SuppressWarnings("deprecation")
	public void initiateDebugSystem() {
		updateDebugModeFromConfig();
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				for(int i = 0 ; i < maxDebugPerFrame ; i++) {
					sendOldestDebug();
				}
			}
		}, 0, 1);
	}
	///////////////////////////////////////////////////////
	//Manager (could be Upper case)
	public ItemStackManager ItemStackManager = new ItemStackManager();
	public GKPlayerManager GKPlayerManager = new GKPlayerManager();
	///////////////////////////////////////////////////////


	///////////////////////////////////////////////////////
	//Other plugins' instance (could be Upper case)
	/*public static boolean pluginOK(String str) {
		return instance.getServer().getPluginManager().isPluginEnabled(str);
	}
	public static boolean GKPartyOK() {
		return pluginOK("GKParty");
	}
	public static boolean GKRoomOK() {
		return pluginOK("GKRoom");
	}
	public static boolean GKSkillOK() {
		return pluginOK("GKSkill");
	}
	public static boolean GKStoreOK() {
		return pluginOK("GKStore");
	}
	 */
	///////////////////////////////////////////////////////
	//Verification
	public Properties getServerProperties() {
		BufferedReader is = null;
		try {
			is = new BufferedReader(new FileReader("server.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}
	public String getProperty(String str) {
		return getServerProperties().getProperty(str);
	}

	///////////////////////////////////////////////////////
	//Version
	public static Version version;
	public static String getUpdateLink() {
		return "http://gkpixel.com";
	}
	public static boolean versionIsBetween(String thisPluginName, String r1, String r2) {
		boolean result = (GKCore.version.isGreaterThan(r1) && GKCore.version.isSmallerThan(r2));
		if (result) {
		}else {
			System.out.println("[ERROR] Cannot load "+thisPluginName+", please use GKCore between "+r1+"-"+r2+", or update both "+thisPluginName+" and GKCore to latest version");

		}
		return result;
	}
	//////////////////////////////////////////////////////////
	//Placeholder API 
	public static String setPlaceholders(Player player, String str) {
		return PlaceholderAPI.setBracketPlaceholders(player, str);
	}
	//////////////////////////////////////////////////////////
	//Server 

	public void initiateSmartInvs() {
        invManager = new InventoryManager(this);
        invManager.init();
	}
	public void initiateMySQL() {
		MYSQLConfig.create();
		MySQL.connect();
	}
	public void initiate() {
		configSystem = new ConfigSystem(this);
		initiateDebugSystem();
		System.out.print("GKCore verified");
		
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				initiateMySQL();
			}
			
		});
		
		initiateSmartInvs();
		
		GKPlayerManager.addAllPlayers();
		
		this.getCommand("gk").setExecutor(new commandClass());//setting commands
		Bukkit.getPluginManager().registerEvents(new Event(), this);
		
		ListEditor.initiate();
		
	}


	@Override
	public void onEnable() {
		plugin = this;
		version = new Version(plugin.getDescription().getVersion());
		instance = this;
		consoleSender = this.getServer().getConsoleSender();
		initiate();
		
	}

	public void onDisable() {
		instance = null;
		ItemStackManager = null;
		GKPlayerManager = null;
	}
	
	//////////////////////////////////////////////////////////

}
