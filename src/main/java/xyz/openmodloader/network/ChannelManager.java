package xyz.openmodloader.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.network.PacketBuffer;

public class ChannelManager {

	private static final BiMap<String, Channel> channels = HashBiMap.create();
	private static final BiMap<String, Integer> ids = HashBiMap.create();

	public static Channel create(String name) {
		return new Channel(name);
	}

	public static void register(String name, Channel channel) {
		channels.put(name, channel);
	}

	public static Channel get(String name) {
		return channels.get(name);
	}

	public static Channel get(int id) {
		return channels.get(getName(id));
	}

	public static String getName(Channel channel) {
		return channels.inverse().get(channel);
	}

	public static String getName(int id) {
		return ids.inverse().get(id);
	}

	public static int getID(Channel channel) {
		return ids.get(getName(channel));
	}

	public static int getID(String name) {
		return ids.get(name);
	}

	public static boolean exists(String channel) {
		return channels.containsKey(channel);
	}

	public static void handle(String channel, PacketBuffer buf) {
		get(channel).handle(buf.readInt(), buf);
	}

}
