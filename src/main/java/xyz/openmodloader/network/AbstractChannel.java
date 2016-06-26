package xyz.openmodloader.network;

import net.minecraft.network.PacketBuffer;

public abstract class AbstractChannel<T extends IPacket> {

	public abstract void write(PacketBuffer buf, T packet);

	public abstract T read(PacketBuffer buf);

	public abstract void handle(T packet);

}
