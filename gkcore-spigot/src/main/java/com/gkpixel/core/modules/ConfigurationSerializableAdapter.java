package com.gkpixel.core.modules;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Type;
import java.util.*;


/**
 * Creates a new instance of Gson for use anywhere
 * <p>
 * Use @GsonIgnore in order to skip serialization and deserialization
 * </p>
 *
 * @return a Gson instance
 */
public class ConfigurationSerializableAdapter implements JsonSerializer<ConfigurationSerializable>, JsonDeserializer<ConfigurationSerializable> {

    final Type objectStringMapType = new TypeToken<Map<String, Object>>() {
    }.getType();

    @Override
    public ConfigurationSerializable deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        final Map<String, Object> map = new LinkedHashMap<>();

        Object result = null;
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            final JsonElement value = entry.getValue();
            final String name = entry.getKey();
            if (value.isJsonObject() && value.getAsJsonObject().has(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                result = this.deserialize(value, value.getClass(), context);
            } else {
                result = context.deserialize(value, Object.class);
            }
            map.put(name, result);
        }
        //Bukkit.getPlayer("hiIamG7").sendMessage("deserialized: "+ConfigurationSerialization.deserializeObject(map));
        try {
            return ConfigurationSerialization.deserializeObject(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(
            ConfigurationSerializable src,
            Type typeOfSrc,
            JsonSerializationContext context) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(src.getClass()));

        Map<String, Object> serialized = new HashMap<>(src.serialize());
        if(serialized.containsKey("display-name")){
            String displayName = serialized.get("display-name").toString();
            BaseComponent[] components = ComponentSerializer.parse(displayName);
            String text = BaseComponent.toLegacyText(components);
            serialized.put("display-name", (Object) text);
        }
        if(serialized.containsKey("lore")){
            List<String> list = new ArrayList<String>((List<String>) serialized.get("lore"));
            for(int i = 0 ; i < list.size() ; i++){
                String line = list.get(i);
                BaseComponent[] components = ComponentSerializer.parse(line);
                String text = BaseComponent.toLegacyText(components);
                list.set(i, text);
            }
            serialized.put("lore", (Object) list);
        }
        map.putAll(serialized);

        return context.serialize(map, objectStringMapType);
    }
}
