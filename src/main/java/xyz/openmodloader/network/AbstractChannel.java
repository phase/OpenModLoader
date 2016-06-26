package xyz.openmodloader.network;

import net.minecraft.network.PacketBuffer;

/**
 * A network channel.
 * @param <T> The packet type.
 */
public abstract class AbstractChannel<T extends AbstractPacket> {

    /**
     * Writes a packet to the buffer.
     * @param buf The buffer to write to
     * @param packet The packet to write
     */
    public abstract void write(PacketBuffer buf, T packet);

    /**
     * Read a packet from the buffer
     * @param buf The buffer to read from
     * @return The packet that was read
     */
    public abstract T read(PacketBuffer buf);

    /**
     * Handle the given packet
     * @param packet The packet to handle
     */
    public abstract void handle(T packet);

}
