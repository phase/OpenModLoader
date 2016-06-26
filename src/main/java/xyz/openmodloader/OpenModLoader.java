package xyz.openmodloader;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openmodloader.event.EventBus;
import xyz.openmodloader.launcher.OMLStrippableTransformer;
import xyz.openmodloader.launcher.strippable.Environment;
import xyz.openmodloader.modloader.ModLoader;
import xyz.openmodloader.modloader.version.UpdateManager;
import xyz.openmodloader.modloader.version.Version;
import xyz.openmodloader.network.Channel;
import xyz.openmodloader.network.ChannelManager;
import xyz.openmodloader.network.DataType;

public enum OpenModLoader {
    INSTANCE;

    private Version mcversion = new Version("1.10.2");
    private Version version = new Version("0.0.1-develop");
    private Logger logger = LogManager.getFormatterLogger("OpenModLoader");
    private EventBus eventBus = new EventBus();
    private Channel channel;
    private SidedHandler sidedHandler;

    public void minecraftConstruction(SidedHandler sidedHandler) {
        this.sidedHandler = sidedHandler;
        getLogger().info("Loading OpenModLoader " + getVersion());
        getLogger().info("Running Minecraft %s on %s using Java %s", mcversion, SystemUtils.OS_NAME, SystemUtils.JAVA_VERSION);
        ModLoader.loadMods();
        UpdateManager.checkForUpdates();
        getSidedHandler().onInitialize();
        channel = ChannelManager.create("oml")
                .createPacket("snackbar")
                    .with("component", DataType.TEXT_COMPONENT)
                    .handle((context, packet) -> OpenModLoader.INSTANCE.getSidedHandler().openSnackbar(packet.get("component", DataType.TEXT_COMPONENT)))
                .build();
    }

    public Version getMinecraftVersion() {
        return mcversion;
    }

    public Version getVersion() {
        return version;
    }

    public Logger getLogger() {
        return logger;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public SidedHandler getSidedHandler() {
        return sidedHandler;
    }

    public Environment getEnvironment() {
        return OMLStrippableTransformer.getEnvironment();
    }

    public Channel getChannel() {
        return channel;
    }
}
