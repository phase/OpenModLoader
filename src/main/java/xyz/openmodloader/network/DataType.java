package xyz.openmodloader.network;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Network data type for serialization/deserialization
 * @param <T> The data type
 */
public class DataType<T> {

	public static final DataType<Boolean> BOOLEAN = new DataType<>(boolean.class, PacketBuffer::writeBoolean, PacketBuffer::readBoolean);
	public static final DataType<Byte> BYTE = new DataType<>(byte.class, (BiConsumer<PacketBuffer, Byte>)PacketBuffer::writeByte, PacketBuffer::readByte);
	public static final DataType<byte[]> BYTE_ARRAY = new DataType<>(byte[].class, PacketBuffer::writeByteArray, PacketBuffer::readByteArray);
	public static final DataType<Short> SHORT = new DataType<>(short.class, (BiConsumer<PacketBuffer, Short>)PacketBuffer::writeByte, PacketBuffer::readShort);
	public static final DataType<Integer> INTEGER = new DataType<>(int.class, PacketBuffer::writeInt, PacketBuffer::readInt);
	public static final DataType<int[]> INTEGER_ARRAY = new DataType<>(int[].class, PacketBuffer::writeVarIntArray, PacketBuffer::readVarIntArray);
	public static final DataType<Float> FLOAT = new DataType<>(float.class, PacketBuffer::writeFloat, PacketBuffer::readFloat);
	public static final DataType<Double> DOUBLE = new DataType<>(double.class, PacketBuffer::writeDouble, PacketBuffer::readDouble);
	public static final DataType<Long> LONG = new DataType<>(long.class, PacketBuffer::writeLong, PacketBuffer::readLong);
	public static final DataType<long[]> LONG_ARRAY = new DataType<>(long[].class, (buf, val) -> {
		buf.writeInt(val.length);
		buf.writeLongArray(val);
	}, buf -> buf.readLongArray(new long[buf.readInt()]));
	public static final DataType<Character> CHAR = new DataType<>(char.class, (BiConsumer<PacketBuffer, Character>)PacketBuffer::writeByte, PacketBuffer::readChar);
	public static final DataType<String> STRING = new DataType<>(String.class, (buf, val) -> {
		buf.writeInt(val.length());
		buf.writeString(val);
	}, buf -> buf.readStringFromBuffer(buf.readInt()));
	public static final DataType<ItemStack> ITEMSTACK = new DataType<>(ItemStack.class, PacketBuffer::writeItemStackToBuffer, PacketBuffer::readItemStackFromBuffer);
	public static final DataType<NBTTagCompound> NBT = new DataType<>(NBTTagCompound.class, PacketBuffer::writeNBTTagCompoundToBuffer, PacketBuffer::readNBTTagCompoundFromBuffer);

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
