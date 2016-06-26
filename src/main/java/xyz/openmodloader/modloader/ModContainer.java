package xyz.openmodloader.modloader;

import java.io.File;

import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.launcher.strippable.Side;

public interface ModContainer {

    ResourceLocation getLogo();

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

    File getSourceFile();

    String[] getTransformers();

    String[] getDependencies();
}