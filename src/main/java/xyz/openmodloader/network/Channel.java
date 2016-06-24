package xyz.openmodloader.network;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class Channel {

	private final String id;
	private final Map<String, PacketSpec> specs;

	Channel(String id) {
		this.id = id;
		this.specs = new HashMap<>();
	}

	public Channel(Channel channel) {
		this.id = channel.id;
		this.specs = ImmutableMap.copyOf(channel.specs);
	}

	public void addPacket(PacketSpec spec) {
		specs.put(spec.getID(), spec);
	}

	public Channel build() {
		Channel immutable = new Channel(this);
		ChannelManager.register(id, immutable);
		return immutable;
	}

	public String getID() {
		return id;
	}

	public boolean hasSpec(String id) {
		return specs.containsKey(id);
	}

	public PacketSpec getSpec(String id) {
		return specs.get(id);
	}

	public Packet send(String id) {
		if (!specs.containsKey(id)) throw new IllegalArgumentException("Invalid PacketSpec id " + id);
		return new Packet(this, specs.get(id));
	}

}
