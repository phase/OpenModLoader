package xyz.openmodloader.network;

import net.minecraft.network.PacketBuffer;

/**
 * @author shadowfacts
 */
public interface IPacket {

	PacketBuffer write(PacketBuffer buf);

	void read(PacketBuffer buf);

	void handle();

}
