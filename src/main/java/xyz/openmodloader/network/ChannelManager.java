package xyz.openmodloader.network;

import java.util.HashMap;
import java.util.Map;

public class ChannelManager {

	private static final Map<String, Channel> channels = new HashMap<>();

	public static Channel create(String id) {
		return new Channel(id);
	}

	static void register(String id, Channel channel) {
		channels.put(id, channel);
	}

	public static Channel get(String id) {
		return channels.get(id);
	}

}
