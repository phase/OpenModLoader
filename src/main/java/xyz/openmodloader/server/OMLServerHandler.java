package xyz.openmodloader.server;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.SidedHandler;
import xyz.openmodloader.launcher.strippable.Side;

public class OMLServerHandler implements SidedHandler {
    private final MinecraftServer server;

    public OMLServerHandler(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void onInitialize() {

    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void openSnackbar(ITextComponent component) {
        OpenModLoader.INSTANCE.getChannel()
                .send("snackbar")
                .set("component", component)
                .toAll();
    }

    @Override
    public EntityPlayerSP getClientPlayer() {
        return null;
    }

    @Override
    public void handleOnMainThread(Runnable runnable) {
        getServer().addScheduledTask(runnable);
    }

    @Override
    public MinecraftServer getServer() {
        return server;
    }
}
