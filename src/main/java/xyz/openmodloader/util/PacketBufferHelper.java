package xyz.openmodloader.util;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Helper class for buffer serialization/deserialization
 */
public class PacketBufferHelper {
    /**
     * Writes a byte to the buffer
     * @param buf The buffer to serialize to
     * @param value The byte to serialize
     */
    public static void writeByte(PacketBuffer buf, byte value) {
        buf.writeByte(value);
    }

    /**
     * Writes a short to the buffer
     * @param buf The buffer to serialize to
     * @param value The short to serialize
     */
    public static void writeShort(PacketBuffer buf, short value) {
        buf.writeShort(value);
    }

    /**
     * Writes a char to the buffer
     * @param buf The buffer to serialize to
     * @param value The char to serialize
     */
    public static void writeChar(PacketBuffer buf, char value) {
        buf.writeChar(value);
    }

    /**
     * Writes a string to the buffer
     * @param buf The buffer to serialize to
     * @param value The string to serialize
     */
    public static void writeString(PacketBuffer buf, String value) {
        buf.writeInt(value.length());
        buf.writeString(value);
    }

    /**
     * Reads a string from the buffer
     * @param buf The buffer to deserialize form
     * @return The deserialized string
     */
    public static String readString(PacketBuffer buf) {
        return buf.readStringFromBuffer(buf.readInt());
    }

    /**
     * Writes a text component to the buffer
     * @param buf The buffer to serialize to
     * @param value The text component to serialize
     */
    public static void writeTextComponent(PacketBuffer buf, ITextComponent value) {
        writeString(buf, ITextComponent.a.componentToJson(value));
    }

    /**
     * Reads a text component from the buffer
     * @param buf The buffer to deserialize form
     * @return The deserialized text component
     */
    public static ITextComponent readTextComponent(PacketBuffer buf) {
        return ITextComponent.a.jsonToComponent(readString(buf));
    }

    /**
     * Writes an array to the buffer
     * @param buf The buffer to serialize to
     * @param value The array to serialize
     * @param writer The function that handles writing to the buffer
     */
    public static <T> void writeArray(PacketBuffer buf, T[] value, BiConsumer<PacketBuffer, T> writer) {
        buf.writeInt(value.length);
        for (T t : value) {
            writer.accept(buf, t);
        }
    }

    /**
     * Reads an array from the buffer
     * @param buf The buffer to deserialize form
     * @param reader The function that handles reading from a buffer
     * @return The deserialized array
     */
    public static <T> T[] readArray(Class<T> clazz, PacketBuffer buf, Function<PacketBuffer, T> reader) {
        T[] value = (T[]) Array.newInstance(clazz, buf.readInt());
        for (int i = 0; i < value.length; i++) {
            value[i] = reader.apply(buf);
        }
        return value;
    }

    /**
     * Writes an NBT list to the buffer
     * @param buf The buffer to serialize to
     * @param value The NBT list to serialize
     */
    public static void writeNBTList(PacketBuffer buf, NBTTagList value) {
        buf.writeInt(value.tagCount());
        for (int i = 0; i < value.tagCount(); i++) {
            buf.writeNBTTagCompoundToBuffer(value.getCompoundTagAt(i));
        }
    }

    /**
     * Reads an NBT list from the buffer
     * @param buf The buffer to deserialize form
     * @return The deserialized NBT list
     */
    public static NBTTagList readNBTList(PacketBuffer buf) {
        NBTTagList value = new NBTTagList();
        for (int i = 0; i < buf.readInt(); i++) {
            value.set(i, buf.readNBTTagCompoundFromBuffer());
        }
        return value;
    }
}
