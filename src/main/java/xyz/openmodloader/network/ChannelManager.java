package xyz.openmodloader.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Manages Name <-> Channel <-> ID mappigns
 */
public class ChannelManager {

	private static final BiMap<String, AbstractChannel> channels = HashBiMap.create();
	private static final BiMap<String, Integer> ids = HashBiMap.create();

	/**
	 * Creates a new channel builder
	 * @param name The name of the channel
	 * @return The channel builder
	 */
	public static Channel create(String name) {
		if (exists(name)) throw new IllegalArgumentException(String.format("Channel %s already exists", name));
		return new Channel(name);
	}

	/**
	 * Registers the given channel for the given name
	 * @param name The name of the channel
	 * @param channel The channel
	 */
	public static void register(String name, AbstractChannel<?> channel) {
		channels.put(name, channel);
	}

	/**
	 * Retrieves the channel with the given name
	 * @param name The name of the channel
	 * @param <T> The packet type of the channel
	 * @return The channel
	 */
	public static <T extends AbstractPacket> AbstractChannel<T> get(String name) {
		return channels.get(name);
	}

	/**
	 * Retrieves the channel with the given ID
	 * @param id The ID of the channel
	 * @param <T> The packet type of the channel
	 * @return The channel
	 */
	public static <T extends AbstractPacket> AbstractChannel<T> get(int id) {
		return channels.get(getName(id));
	}

	/**
	 * Retrieves the name of the given channel
	 * @param channel The channel
	 * @return The name of the channel
	 */
	public static String getName(AbstractChannel<?> channel) {
		return channels.inverse().get(channel);
	}

	/**
	 * Retrieves the name of the channel with the given ID
	 * @param id The ID
	 * @return The name
	 */
	public static String getName(int id) {
		return ids.inverse().get(id);
	}

	/**
	 * Retrieves the ID of the given channel
	 * @param channel The channel
	 * @return The ID
	 */
	public static int getID(AbstractChannel<?> channel) {
		return ids.get(getName(channel));
	}

	/**
	 * Retrieves the ID of the channel with the given name
	 * @param name The name
	 * @return The ID
	 */
	public static int getID(String name) {
		return ids.get(name);
	}

	/**
	 * Checks if a channel with the given name is already registered
	 * @param name The name
	 * @return If the channel exists
	 */
	public static boolean exists(String name) {
		return channels.containsKey(name);
	}

}
