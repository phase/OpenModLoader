package xyz.openmodloader.modloader;

import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.launcher.strippable.Side;

import java.io.File;

public interface ModContainer {

    String getLogo();

    ResourceLocation getLogoTexture();

    IMod getInstance();

    Version getVersion();

    Version getMinecraftVersion();

    String getModID();

    String getName();

    String getDescription();

    Side getSide();

    String getAuthor();

    String getURL();

    String getUpdateURL();

    String[] getTransformers();

    String[] getDependencies();

    File getOriginFile();
}