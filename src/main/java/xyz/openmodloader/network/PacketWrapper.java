package xyz.openmodloader.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class PacketWrapper implements net.minecraft.network.Packet<INetHandler> {

	private Packet packet;

	public PacketWrapper(Packet packet) {
		this.packet = packet;
	}

	public PacketWrapper() {

	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		Channel channel = ChannelManager.get(buf.readInt());
		PacketSpec spec = channel.getSpec(buf.readInt());
		packet = new Packet(channel, spec);
		packet.read(buf);
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeInt(ChannelManager.getID(packet.channel));
		buf.writeInt(packet.channel.getID(packet.spec));
		packet.write(buf);
	}

	@Override
	public void processPacket(INetHandler handler) {

	}

}
