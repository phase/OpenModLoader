package xyz.openmodloader.network;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.util.PacketBufferHelper;

/**
 * Network data types for serialization/deserialization
 */
public class DataTypes {
    public static final DataType<Boolean> BOOLEAN = new DataType<>(Boolean.class, PacketBuffer::writeBoolean, PacketBuffer::readBoolean);
    public static final ArrayDataType<Boolean> BOOLEAN_ARRAY = new ArrayDataType<>(Boolean[].class, BOOLEAN);
    public static final DataType<Byte> BYTE = new DataType<>(Byte.class, PacketBufferHelper::writeByte, PacketBuffer::readByte);
    public static final ArrayDataType<Byte> BYTE_ARRAY = new ArrayDataType<>(Byte[].class, BYTE);
    public static final DataType<Short> SHORT = new DataType<>(Short.class, PacketBufferHelper::writeShort, PacketBuffer::readShort);
    public static final ArrayDataType<Short> SHORT_ARRAY = new ArrayDataType<>(Short[].class, SHORT);
    public static final DataType<Integer> INTEGER = new DataType<>(Integer.class, PacketBuffer::writeInt, PacketBuffer::readInt);
    public static final ArrayDataType<Integer> INTEGER_ARRAY = new ArrayDataType<>(Integer[].class, INTEGER);
    public static final DataType<Float> FLOAT = new DataType<>(Float.class, PacketBuffer::writeFloat, PacketBuffer::readFloat);
    public static final ArrayDataType<Float> FLOAT_ARRAY = new ArrayDataType<>(Float[].class, FLOAT);
    public static final DataType<Double> DOUBLE = new DataType<>(Double.class, PacketBuffer::writeDouble, PacketBuffer::readDouble);
    public static final ArrayDataType<Double> DOUBLE_ARRAY = new ArrayDataType<>(Double[].class, DOUBLE);
    public static final DataType<Long> LONG = new DataType<>(Long.class, PacketBuffer::writeLong, PacketBuffer::readLong);
    public static final ArrayDataType<Long> LONG_ARRAY = new ArrayDataType<>(Long[].class, LONG);
    public static final DataType<Character> CHARACTER = new DataType<>(Character.class, PacketBufferHelper::writeChar, PacketBuffer::readChar);
    public static final ArrayDataType<Character> CHARACTER_ARRAY = new ArrayDataType<>(Character[].class, CHARACTER);
    public static final DataType<String> STRING = new DataType<>(String.class, PacketBufferHelper::writeString, PacketBufferHelper::readString);
    public static final ArrayDataType<String> STRING_ARRAY = new ArrayDataType<>(String[].class, STRING);
    public static final DataType<ItemStack> ITEMSTACK = new DataType<>(ItemStack.class, PacketBuffer::writeItemStackToBuffer, PacketBuffer::readItemStackFromBuffer);
    public static final ArrayDataType<ItemStack> ITEMSTACK_ARRAY = new ArrayDataType<>(ItemStack[].class, ITEMSTACK);
    public static final DataType<NBTTagCompound> NBT = new DataType<>(NBTTagCompound.class, PacketBuffer::writeNBTTagCompoundToBuffer, PacketBuffer::readNBTTagCompoundFromBuffer);
    public static final DataType<NBTTagList> NBT_LIST = new DataType<>(NBTTagList.class, PacketBufferHelper::writeNBTList, PacketBufferHelper::readNBTList);
    public static final DataType<ITextComponent> TEXT_COMPONENT = new DataType<>(ITextComponent.class, PacketBufferHelper::writeTextComponent, PacketBufferHelper::readTextComponent);
}
