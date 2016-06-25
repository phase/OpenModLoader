package xyz.openmodloader.network;

import com.google.common.collect.ImmutableMap;
import xyz.openmodloader.event.strippable.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PacketSpec {

	private final Channel channel;
	final String id;
	final Map<String, DataType> types;
	BiConsumer<Context, Packet> handler;

	PacketSpec(Channel builder, String id) {
		this.channel = builder;
		this.id = id;
		this.types = new HashMap<>();
	}

	private PacketSpec(PacketSpec spec) {
		this.channel = null;
		this.id = spec.id;
		this.types = ImmutableMap.copyOf(spec.types);
		this.handler = spec.handler;
	}

	public PacketSpec with(String id, DataType type) {
		if (isImmutable()) throw new IllegalStateException("Cannot use PacketSpec.with on an immutable clone");
		if (types.containsKey(id)) throw new IllegalArgumentException(String.format("ID %s is already set to DataType %s", id, types.get(id)));

		types.put(id, type);

		return this;
	}

	public Channel handle(BiConsumer<Context, Packet> handler) {
		if (isImmutable()) throw new IllegalStateException("Cannot use PacketSpec.handle on an immutable clone");
		if (this.handler != null) throw new IllegalStateException(String.format("Packet %s already has a handler, %s", id, this.handler));

		this.handler = handler;
		channel.addPacket(new PacketSpec(this));
		return channel;
	}

	public Channel handle(BiConsumer<Context, Packet> clientHandler, BiConsumer<Context, Packet> serverHandler) {
		if (isImmutable()) throw new IllegalStateException("Cannot use PacketSpec.handle on an immutable clone");
		if (this.handler != null) throw new IllegalStateException(String.format("Packet %s already has a handler, %s", id, this.handler));

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
