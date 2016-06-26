package xyz.openmodloader.server;

import net.minecraft.server.MinecraftServer;

public class OMLServerHelper {

	private static MinecraftServer server;

	public static MinecraftServer getServer() {
		return server;
	}

	public static void setServer(MinecraftServer server) {
		OMLServerHelper.server = server;
	}

}
