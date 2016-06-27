package xyz.openmodloader.modloader;

import java.io.File;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.modloader.version.UpdateManager;
import xyz.openmodloader.modloader.version.Version;

/**
 * The Interface ModContainer.
 */
public interface ModContainer {
    
    /**
     * Gets the logo.
     *
     * @return the logo
     */
    String getLogo();

    /**
     * Gets the logo texture.
     *
     * @return the logo texture
     */
    ResourceLocation getLogoTexture();

    /**
     * Gets the {@link Mod} instance for this container.
     * May be null.
     *
     * @return single instance of ModContainer
     */
    Mod getInstance();

    /**
     * Gets the mod version.
     *
     * @return the mod version
     */
    Version getVersion();

    /**
     * Gets the expected Minecraft version for the mod.
     *
     * @return the expected Minecraft version
     */
    Version getMinecraftVersion();

    /**
     * Gets the mod ID. The ID is an unique, non-empty string
     * containing a combination of the characters a-z, 0-9, _ and -.
     * This method must always return the same value.
     *
     * @return the mod ID
     */
    String getModID();

    /**
     * Gets the mod name. Cannot be empty.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the description. May be null.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Gets the mod side. If the current environment
     * does not match the expected one, the mod will not load.
     *
     * @return the expected side
     */
    Side getSide();

    /**
     * Gets the mod author. Should be a comma separated list
     * of contributors.
     *
     * @return the author
     */
    String getAuthor();

    /**
     * Gets the home URL of this mod. Must be either null or a valid URL.
     *
     * @return the URL
     */
    String getURL();

    /**
     * Gets the URL to the update checker JSON for this mod. May be null.
     * A custom update checker may be registered via {@link UpdateManager#registerUpdater()}.
     *
     * @return the update URL
     */
    String getUpdateURL();

    /**
     * Gets the file this mod was loaded from. May be null.
     *
     * @return the mod file
     */
    File getModFile();

    /**
     * Gets the list of transformers for this mod. Each string in the array
     * should be the full name of a class implementing {@link IClassTransformer}.
     *
     * @return the transformers
     */
    String[] getTransformers();

    /**
     * Gets the list of dependencies for this mod. Each string should contain the
     * mod ID of the dependency, and optionally, a minimum version. Example:
     * <code>examplemod:1.0.0</code>. The string may also start with "optional ",
     * in which case the loader won't check whether the mod is loaded, but will
     * sort the mods so that the dependant comes after the dependency if the dependency
     * is loaded.
     *
     * @return the dependencies
     */
    String[] getDependencies();
}