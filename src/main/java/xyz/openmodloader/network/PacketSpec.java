package xyz.openmodloader.network;

import com.google.common.collect.ImmutableMap;
import xyz.openmodloader.event.strippable.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * The specification for a packet.
 */
public class PacketSpec {

	private final Channel channel;
	final String name;
	final Map<String, DataType> types;
	BiConsumer<Context, Packet> handler;

	/**
	 * Creates a mutable packet specification builder for the given channele and name.
	 * @param channel The channel this packet is for
	 * @param name The name of this packet
	 */
	PacketSpec(Channel channel, String name) {
		this.channel = channel;
		this.name = name;
		this.types = new HashMap<>();
	}

	/**
	 * Creates a finalized immutable copy of this packet
	 * @param spec The mutable builder spec
	 */
	private PacketSpec(PacketSpec spec) {
		this.channel = null;
		this.name = spec.name;
		this.types = ImmutableMap.copyOf(spec.types);
		this.handler = spec.handler;
	}

	/**
	 * Adds a data type for the given ID
	 * @param id The ID of the type to add
	 * @param type The type to add
	 * @return This mutable builder instance
	 */
	public PacketSpec with(String id, DataType type) {
		if (isImmutable()) throw new IllegalStateException("Cannot use PacketSpec.with on an immutable clone");
		if (types.containsKey(id)) throw new IllegalArgumentException(String.format("ID %s is already set to DataType %s", id, types.get(id)));

		types.put(id, type);

		return this;
	}

	/**
	 * Sets the handler for this packet spec.
	 * Finalizes this packet and registers it with the channel.
	 * @param handler The packet handler
	 * @return The channel
	 */
	public Channel handle(BiConsumer<Context, Packet> handler) {
		if (isImmutable()) throw new IllegalStateException("Cannot use PacketSpec.handle on an immutable clone");
		if (this.handler != null) throw new IllegalStateException(String.format("Packet %s already has a handler, %s", name, this.handler));

		this.handler = handler;
		channel.addPacket(new PacketSpec(this));
		return channel;
	}

	/**
	 * Sets the handler for this packet spec.
	 * Finalizes this packet and registers it with the channel.
	 * @param clientHandler The client side handler.
	 * @param serverHandler The server side handler.
	 * @return The channel
	 */
	public Channel handle(BiConsumer<Context, Packet> clientHandler, BiConsumer<Context, Packet> serverHandler) {
		if (isImmutable()) throw new IllegalStateException("Cannot use PacketSpec.handle on an immutable clone");
		if (this.handler != null) throw new IllegalStateException(String.format("Packet %s already has a handler, %s", name, this.handler));

		this.handler = (context, packet) -> {
			if (context.getSide() == Side.CLIENT) {
				clientHandler.accept(context, packet);
			} else if (context.getSide() == Side.SERVER) {
				serverHandler.accept(context, packet);
			}
		};
		channel.addPacket(new PacketSpec(this));
		return channel;
	}

	private boolean isImmutable() {
		return channel == null;
	}

}
