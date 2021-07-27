package me.GK.core.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

//TODO add your package name here

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import me.GK.core.main.GKCore;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;


/**
 * Creates a new instance of Gson for use anywhere
 * <p>
 * Use @GsonIgnore in order to skip serialization and deserialization
 * </p>
 * 
 * @return a Gson instance
 */
public class JsonSystem{
	public static JsonSystem create() {
		return new JsonSystem();
	}
	public JsonSystem() {
		
	}
	public GsonBuilder builder = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.disableHtmlEscaping()
			.setPrettyPrinting()
			;
	
	public Gson gson = builder.create();
	public <T> void registerAbstractClass(Class<T> c) {
		gson=builder.create();
		GKCore.debug("registered abstract class : "+c.getSimpleName()+"  successfully");
	}
	public <T> void registerClass(Class<T> c, Object adaptor) {
		builder.registerTypeAdapter(c, adaptor);
		gson=builder.create();
		GKCore.debug("registered custom class : "+c.getSimpleName()+"  successfully");
	}
}
