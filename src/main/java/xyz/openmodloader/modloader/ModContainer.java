package xyz.openmodloader.modloader;

import net.minecraft.util.ResourceLocation;

public interface ModContainer {

    ResourceLocation getLogo();

    IMod getInstance();

    Version getVersion();

    String getModID();

    String getName();

    String getDescription();

    String getAuthor();

    String getURL();

    String getUpdateURL();

    String[] getTransformers();

    String[] getDependencies();
}