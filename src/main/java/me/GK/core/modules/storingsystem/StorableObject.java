package me.GK.core.modules.storingsystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.GK.core.main.Extensions;
import me.GK.core.main.GKCore;
import me.GK.core.modules.storingsystem.StorableObjectDatabase.DatabaseType;
import net.md_5.bungee.api.ChatColor;
public abstract class StorableObject {
	public boolean saving = false;//this mean the file is uploading to mysql or saving to local. Prevent multiple save at the same time
	public boolean needToSave = false;//when use save(). this will become true. wait for next loop then it would upload to database
	///////////////////////////////////////////////////////////////////////////////////////
	//Changable methods
	public abstract StorableObjectDatabase getDatabase();
	public abstract String getID();
	///////////////////////////////////////////////////////////////////////////////////////

	//Saving
	public void initiate() {
		
	}
	public void save() {
		needToSave = true;
	}
	////////////////////////////////////////////////////////////////////////////////

	
}
