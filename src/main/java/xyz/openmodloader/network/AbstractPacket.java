package xyz.openmodloader.network;

import net.minecraft.network.PacketBuffer;

public abstract class AbstractPacket {

	public abstract PacketBuffer write(PacketBuffer buf);

	public abstract void read(PacketBuffer buf);

	public abstract void handle();

}
