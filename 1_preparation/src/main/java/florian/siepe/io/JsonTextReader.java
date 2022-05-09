package florian.siepe.io;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class JsonTextReader<T> implements InputReader<T> {

    private final Class<T> typeClass;

    public JsonTextReader(Class<T> clazz) {
        this.typeClass = clazz;
    }

    public static final Jsonb JSONB = JsonbBuilder.create();

    @Override
    public T read(final File file) {
        final var lines = readLines(file);
        if (!lines.isEmpty()) {
            return read(lines.get(0));
        } else {
            return null;
        }
    }

    protected List<String> readLines(final File file) {
        try {
            return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    protected T read(final String content) {
        return JSONB.fromJson(content, typeClass);
    }
}
