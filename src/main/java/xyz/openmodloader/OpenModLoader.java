package xyz.openmodloader;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.DedicatedServer;
import xyz.openmodloader.event.EventBus;
import xyz.openmodloader.launcher.OMLStrippableTransformer;
import xyz.openmodloader.launcher.strippable.Environment;
import xyz.openmodloader.modloader.ModLoader;
import xyz.openmodloader.modloader.version.UpdateManager;
import xyz.openmodloader.modloader.version.Version;
import xyz.openmodloader.network.Channel;
import xyz.openmodloader.network.ChannelManager;
import xyz.openmodloader.network.DataTypes;

public enum OpenModLoader {
    INSTANCE;

    private Version mcversion = new Version("1.10.2");
    private Version version = new Version("0.0.1-develop");
    private Logger logger = LogManager.getFormatterLogger("OpenModLoader");
    private EventBus eventBus = new EventBus();
    private Channel channel;
    private SidedHandler sidedHandler;

    /**
     * Initializes the API and starts mod loading.
     * Called from {@link Minecraft#startGame()} and
     * {@link DedicatedServer#startServer()}.
     *
     * @param sidedHandler the sided handler
     */
    public void minecraftConstruction(SidedHandler sidedHandler) {
        this.sidedHandler = sidedHandler;
        getLogger().info("Loading OpenModLoader " + getVersion());
        getLogger().info("Running Minecraft %s on %s using Java %s", mcversion, SystemUtils.OS_NAME, SystemUtils.JAVA_VERSION);
        ModLoader.loadMods();
        UpdateManager.checkForUpdates();
        getSidedHandler().onInitialize();
        channel = ChannelManager.create("oml")
                .createPacket("snackbar")
                    .with("component", DataTypes.TEXT_COMPONENT)
                    .handle((context, packet) -> OpenModLoader.INSTANCE.getSidedHandler().openSnackbar(packet.get("component", DataTypes.TEXT_COMPONENT)))
                .build();
    }

    /**
     * Gets the current Minecraft version.
     *
     * @return the Minecraft version
     */
    public Version getMinecraftVersion() {
        return mcversion;
    }

    /**
     * Gets the current OML version.
     *
     * @return the OML version
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Gets the OML logger. Primarily intended for internal use.
     * Mods should call {@link LogManager#getLogger(String)} to get
     * a properly named logger.
     *
     * @return the OML logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets the main event bus. All OML events are fired here.
     *
     * @return the event bus
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Gets the sided handler.
     *
     * @return the sided handler
     */
    public SidedHandler getSidedHandler() {
        return sidedHandler;
    }

    /**
     * Gets the type of the current runtime environment.
     *
     * @return the environment
     */
    public Environment getEnvironment() {
        return OMLStrippableTransformer.getEnvironment();
    }

    /**
     * Gets OMLs internal networking channel.
     *
     * @return the channel
     */
    public Channel getChannel() {
        return channel;
    }
}
