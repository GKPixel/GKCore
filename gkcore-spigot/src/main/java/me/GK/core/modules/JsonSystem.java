package me.GK.core.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.GK.core.GKCore;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.meta.BannerMeta;


/**
 * Creates a new instance of Gson for use anywhere
 * <p>
 * Use @GsonIgnore in order to skip serialization and deserialization
 * </p>
 */
public class JsonSystem {
    public GsonBuilder builder = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
            .registerTypeHierarchyAdapter(BannerMeta.class, new BannerMetaAdaptor());
    public Gson gson = builder.create();

    public JsonSystem() {

    }

    public static JsonSystem create() {
        return new JsonSystem();
    }

    public <T> void registerAbstractClass(Class<T> c) {
        builder.registerTypeAdapter(c, new InterfaceAdapter<T>());
        gson = builder.create();
        GKCore.debug("registered abstract class : " + c.getSimpleName() + "  successfully");
    }

    public <T> void registerClass(Class<T> c, Object adaptor) {
        builder.registerTypeAdapter(c, adaptor);
        gson = builder.create();
        GKCore.debug("registered custom class : " + c.getSimpleName() + "  successfully");
    }
}
