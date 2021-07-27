package me.GK.core.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.GK.core.main.GKCore;


/**
 * Creates a new instance of Gson for use anywhere
 * <p>
 * Use @GsonIgnore in order to skip serialization and deserialization
 * </p>
 *
 * @return a Gson instance
 */
public class JsonSystem {
    public GsonBuilder builder = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .disableHtmlEscaping()
            .setPrettyPrinting();
    public Gson gson = builder.create();

    public JsonSystem() {

    }

    public static JsonSystem create() {
        return new JsonSystem();
    }

    public <T> void registerAbstractClass(Class<T> c) {
        gson = builder.create();
        GKCore.debug("registered abstract class : " + c.getSimpleName() + "  successfully");
    }

    public <T> void registerClass(Class<T> c, Object adaptor) {
        builder.registerTypeAdapter(c, adaptor);
        gson = builder.create();
        GKCore.debug("registered custom class : " + c.getSimpleName() + "  successfully");
    }
}
