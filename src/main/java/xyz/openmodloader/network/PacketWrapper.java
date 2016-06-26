package xyz.openmodloader.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class PacketWrapper implements net.minecraft.network.Packet<INetHandler> {

	private AbstractChannel channel;
	private IPacket packet;

	public PacketWrapper(AbstractChannel channel, IPacket packet) {
		this.channel = channel;
		this.packet = packet;
	}

	public PacketWrapper() {

	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		AbstractChannel<?> channel = ChannelManager.get(buf.readInt());
		packet = channel.read(buf);
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeInt(ChannelManager.getID(channel));
		channel.write(buf, packet);
	}

	@Override
	public void processPacket(INetHandler netHandler) {
		channel.handle(packet);
	}

}
