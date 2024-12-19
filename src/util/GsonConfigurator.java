package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import handlers.adapters.DurationTypeAdapter;
import handlers.adapters.LocalDateAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonConfigurator {
    public static Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }
}
