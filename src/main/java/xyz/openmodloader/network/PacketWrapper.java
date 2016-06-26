package xyz.openmodloader.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

/**
 * Minecraft wrapper packet for actually sending packets on the network.
 */
public class PacketWrapper implements net.minecraft.network.Packet<INetHandler> {

	private AbstractChannel channel;
	private AbstractPacket packet;

	/**
	 * Creates a new wrapper packet
	 * @param channel The OML channel this packet belongs to
	 * @param packet The OML packet this wrapper corresponds to
	 */
	public PacketWrapper(AbstractChannel channel, AbstractPacket packet) {
		this.channel = channel;
		this.packet = packet;
	}

	/**
	 * No-args constructor deserialization.
	 */
	public PacketWrapper() {

	}

	/**
	 * Reads the packet from the given buffer
	 * @param buf The buffer
	 * @throws IOException
	 */
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		AbstractChannel<?> channel = ChannelManager.get(buf.readInt());
		packet = channel.read(buf);
	}

	/**
	 * Writes the packet to the given buffer
	 * @param buf The buffer
	 * @throws IOException
	 */
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeInt(ChannelManager.getID(channel));
		channel.write(buf, packet);
	}

	/**
	 * Handles recieving this packet
	 * @param netHandler
	 */
	@Override
	public void processPacket(INetHandler netHandler) {
		channel.handle(packet);
	}

}
