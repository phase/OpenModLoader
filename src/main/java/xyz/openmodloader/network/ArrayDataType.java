package xyz.openmodloader.network;

import xyz.openmodloader.util.PacketBufferHelper;

/**
 * Network array data type for serialization/deserialization
 * @param <T> The data type
 */
public class ArrayDataType<T> extends DataType<T[]> {
    /**
     * Creates a new array data type
     * @param clazz The array type
     * @param baseType The base DataType
     */
    public ArrayDataType(Class<T[]> clazz, DataType<T> baseType) {
        super(clazz,
                (buf, val) -> PacketBufferHelper.writeArray(buf, val, baseType::write),
                (buf) -> PacketBufferHelper.readArray(baseType.getClazz(), buf, baseType::read));
    }
}
