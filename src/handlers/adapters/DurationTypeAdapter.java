package handlers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter writer, Duration value) throws IOException {
        writer.value(value.toMinutes());
    }

    @Override
    public Duration read(JsonReader reader) throws IOException {
        return Duration.ofMinutes(reader.nextInt());
    }
}
