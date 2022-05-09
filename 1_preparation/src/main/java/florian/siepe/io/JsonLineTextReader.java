package florian.siepe.io;

import java.io.File;
import java.util.List;

public class JsonLineTextReader<T> implements InputReader<List<T>> {
    private final JsonTextReader<T> jsonReader;


    public JsonLineTextReader(Class<T> clazz) {
        this.jsonReader = new JsonTextReader<>(clazz);
    }

    @Override
    public List<T> read(final File file) {
        return jsonReader.readLines(file).stream().map(jsonReader::read).toList();
    }
}
