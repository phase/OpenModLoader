package xyz.openmodloader.launcher;

import net.minecraft.server.MinecraftServer;

public class OMLServerTweaker extends OMLTweaker {

    @Override
    public String getLaunchTarget() {
        return MinecraftServer.class.getCanonicalName();
    }
}
