package xyz.openmodloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openmodloader.event.EventBus;
import xyz.openmodloader.launcher.OMLStrippableTransformer;
import xyz.openmodloader.launcher.strippable.Environment;
import xyz.openmodloader.modloader.ModLoader;
import xyz.openmodloader.modloader.Version;

public enum OpenModLoader {
    INSTANCE;

    private Version version = new Version("0.0.1-develop");
    private Logger logger = LogManager.getLogger();
    private EventBus eventBus = new EventBus();
    private ISidedHandler sidedHandler;

    public void minecraftConstruction(ISidedHandler sidedHandler) {
        this.sidedHandler = sidedHandler;
        getLogger().info("Loading OpenModLoader " + getVersion());
        ModLoader.registerMods();
        getSidedHandler().onInitialize();
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

    public ISidedHandler getSidedHandler() {
        return sidedHandler;
    }

    public Environment getEnvironment() {
        return OMLStrippableTransformer.getEnvironment();
    }
}
