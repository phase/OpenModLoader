package xyz.openmodloader.network;

import net.minecraft.network.PacketBuffer;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Network data type for serialization/deserialization
 * @param <T> The data type
 */
public class DataType<T> {
    private final Class<T> clazz;
    private final BiConsumer<PacketBuffer, T> writer;
    private final Function<PacketBuffer, T> reader;

    /**
     * Creates a new data type
     * @param clazz The type
     * @param writer The function that handles writing to a buffer
     * @param reader THe function that handles reading from a buffer
     */
    public DataType(Class<T> clazz, BiConsumer<PacketBuffer, T> writer, Function<PacketBuffer, T> reader) {
        this.clazz = clazz;
        this.writer = writer;
        this.reader = reader;
    }

    /**
     * Writes an instance of the data type to the buffer
     * @param buf The buffer to serialize to
     * @param value The value to serialize
     */
    public void write(PacketBuffer buf, T value) {
        writer.accept(buf, value);
    }

    /**
     * Reads an instance of the data type from the buffer
     * @param buf The buffer to deserialize form
     * @return The deserialized value
     */
    public T read(PacketBuffer buf) {
        return reader.apply(buf);
    }

    /**
     * @return The class of the data type
     */
    public Class<T> getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "DataType{" +
                "clazz=" + clazz +
                '}';
    }
}
