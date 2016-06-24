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
		String channelID = buf.readStringFromBuffer(buf.readInt());
		String id = buf.readStringFromBuffer(buf.readInt());
		Channel channel = ChannelManager.get(channelID);
		packet = new Packet(channel, channel.getSpec(id));
		packet.read(buf);
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeInt(packet.getChannelID().length());
		buf.writeString(packet.getChannelID());
		buf.writeInt(packet.getID().length());
		buf.writeString(packet.getID());
		packet.write(buf);
	}

	@Override
	public void processPacket(INetHandler handler) {

	}

}
