package xyz.openmodloader.network;

import net.minecraft.network.PacketBuffer;

/**
 * A network packet to be sent on a {@link AbstractChannel}
 */
public abstract class AbstractPacket {

    /**
     * Writes this packet to the given buffer
     * @param buf The buffer
     * @return The buffer, for convenience
     */
    public abstract PacketBuffer write(PacketBuffer buf);

    /**
     * Reads this packet from the given buffer
     * @param buf The buffer
     */
    public abstract void read(PacketBuffer buf);

    /**
     * Handles receiving this packet
     */
    public abstract void handle();

}
