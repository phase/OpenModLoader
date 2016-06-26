package xyz.openmodloader.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.network.PacketBuffer;

public class ChannelManager {

	private static final BiMap<String, AbstractChannel> channels = HashBiMap.create();
	private static final BiMap<String, Integer> ids = HashBiMap.create();

	public static Channel create(String name) {
		return new Channel(name);
	}

	public static void register(String name, AbstractChannel<?> channel) {
		channels.put(name, channel);
	}

	public static <T extends IPacket> AbstractChannel<T> get(String name) {
		return channels.get(name);
	}

	public static <T extends IPacket> AbstractChannel<T> get(int id) {
		return channels.get(getName(id));
	}

	public static String getName(AbstractChannel<?> channel) {
		return channels.inverse().get(channel);
	}

	public static String getName(int id) {
		return ids.inverse().get(id);
	}

	public static int getID(AbstractChannel<?> channel) {
		return ids.get(getName(channel));
	}

	public static int getID(String name) {
		return ids.get(name);
	}

	public static boolean exists(String channel) {
		return channels.containsKey(channel);
	}

}
