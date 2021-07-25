package me.GK.core.modules;

//TODO add your package name here

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;


/**
 * Creates a new instance of Gson for use anywhere
 * <p>
 * Use @GsonIgnore in order to skip serialization and deserialization
 * </p>
 * 
 * @return a Gson instance
 */
public class ConfigurationSerializableAdapter implements JsonSerializer<ConfigurationSerializable>, JsonDeserializer<ConfigurationSerializable> {

    final Type objectStringMapType = new TypeToken<Map<String, Object>>() {}.getType();

    @Override
    public ConfigurationSerializable deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException
    {
        final Map<String, Object> map = new LinkedHashMap<>();

        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            final JsonElement value = entry.getValue();
            final String name = entry.getKey();

            if (value.isJsonObject() && value.getAsJsonObject().has(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                map.put(name, this.deserialize(value, value.getClass(), context));
            } else {
                map.put(name, context.deserialize(value, Object.class));
            }
        }
        //Bukkit.getPlayer("hiIamG7").sendMessage("deserialized: "+ConfigurationSerialization.deserializeObject(map));
        try {
        	return ConfigurationSerialization.deserializeObject(map);
        }catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }

    @Override
    public JsonElement serialize(
            ConfigurationSerializable src,
            Type typeOfSrc,
            JsonSerializationContext context)
    {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(src.getClass()));
        map.putAll(src.serialize());
        return context.serialize(map, objectStringMapType);
    }
}
