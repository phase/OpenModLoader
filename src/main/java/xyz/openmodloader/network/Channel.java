package xyz.openmodloader.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

public class Channel {

	final String id;
	private final BiMap<String, PacketSpec> specs;
	private final BiMap<String, Integer> specIDs;

	Channel(String id) {
		this.id = id;
		this.specs = HashBiMap.create();
		this.specIDs = HashBiMap.create();
	}

	public Channel(Channel channel) {
		this.id = channel.id;
		this.specs = ImmutableBiMap.copyOf(channel.specs);
		this.specIDs = ImmutableBiMap.copyOf(channel.specIDs);
	}

	public PacketSpec createPacket(String id) {
		return new PacketSpec(this, id);
	}

	void addPacket(PacketSpec spec) {
		specs.put(spec.id, spec);
	}

	public Channel build() {
		Channel immutable = new Channel(this);
		ChannelManager.register(id, immutable);
		return immutable;
	}

	public Packet send(String id) {
		if (!specs.containsKey(id)) throw new IllegalArgumentException("Invalid PacketSpec id " + id);
		return new Packet(this, specs.get(id));
	}

	public PacketSpec getSpec(String name) {
		return specs.get(name);
	}

	public PacketSpec getSpec(int id) {
		return specs.get(getName(id));
	}

	public String getName(PacketSpec spec) {
		return specs.inverse().get(spec);
	}

	public String getName(int id) {
		return specIDs.inverse().get(id);
	}

	public int getID(PacketSpec spec) {
		return specIDs.get(getName(spec));
	}

	public int getID(String name) {
		return specIDs.get(name);
	}

}
