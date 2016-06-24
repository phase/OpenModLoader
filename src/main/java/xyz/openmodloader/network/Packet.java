package xyz.openmodloader.network;

import net.minecraft.network.PacketBuffer;

import java.util.HashMap;
import java.util.Map;

public class Packet {

	private final String id;
	private final Channel channel;
	private final PacketSpec spec;
	final Map<String, Object> values = new HashMap<>();
	final Map<String, DataType> types = new HashMap<>();

	public Packet(Channel channel, PacketSpec spec) {
		this.id = spec.getID();
		this.channel = channel;
		this.spec = spec;

	}

	public Packet set(String id, Object value) {
		if (!spec.types.containsKey(id)) throw new IllegalArgumentException("No such key " + id);
		if (!spec.types.get(id).getClazz().isAssignableFrom(value.getClass())) throw new IllegalArgumentException(String.format("Key %s expected type %s, got %s", id, spec.types.get(id).getClazz(), value.getClass()));
		if (values.containsKey(id)) throw new IllegalArgumentException(String.format("Key %s is already set to %s", id, values.get(id)));

		values.put(id, value);
		types.put(id, spec.types.get(id));

		return this;
	}

	public <T> T get(String id, DataType<T> type) {
		if (!values.containsKey(id)) throw new IllegalArgumentException("No such key " + id);
		if (!types.get(id).equals(type)) throw new IllegalArgumentException(String.format("Wrong type for key %s, %s is registered but got %s", id, types.get(id), type));
		return (T)values.get(id);
	}

	void write(PacketBuffer buf) {
		values.forEach((id, value) -> {
			types.get(id).write(buf, value);
		});
	}

	void read(PacketBuffer buf) {
		types.forEach((id, type) -> {
			values.put(id, type.read(buf));
		});
	}

	public String getID() {
		return id;
	}

	public String getChannelID() {
		return channel.getID();
	}

}
