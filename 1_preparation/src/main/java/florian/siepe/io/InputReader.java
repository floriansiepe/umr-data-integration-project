package florian.siepe.io;

import java.io.File;

public interface InputReader<T> {
    T read(File file);
}
