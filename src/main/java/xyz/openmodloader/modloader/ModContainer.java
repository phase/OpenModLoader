package xyz.openmodloader.modloader;

import java.io.File;

import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.modloader.version.Version;

public interface ModContainer {
    String getLogo();

    ResourceLocation getLogoTexture();

    Mod getInstance();

    Version getVersion();

    Version getMinecraftVersion();

    String getModID();

    String getName();

    String getDescription();

    Side getSide();

    String getAuthor();

    String getURL();

    String getUpdateURL();

    File getModFile();

    String[] getTransformers();

    String[] getDependencies();
}