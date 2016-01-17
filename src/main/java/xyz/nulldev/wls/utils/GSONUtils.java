package xyz.nulldev.wls.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class GSONUtils {
    static Gson GSON = null;
    public static Gson getGson() {
        if(GSON == null) {
            GSON = new GsonBuilder().setPrettyPrinting().create();
        }
        return GSON;
    }
}
