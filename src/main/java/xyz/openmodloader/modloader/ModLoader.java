package xyz.openmodloader.modloader;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.launchwrapper.LaunchClassLoader;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.SidedHandler;
import xyz.openmodloader.launcher.OMLTweaker;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.modloader.version.JsonUpdateContainer;
import xyz.openmodloader.modloader.version.UpdateManager;

/**
 * The class responsible for registering and loading mods.
 * Loads mods from the mods folder and the classpath.
 * Mods are defined by the MANIFEST.MF file of the mod jar.
 * See {@link ManifestModContainer} for more info.
 */
public class ModLoader {

    /**
     * A list of all loaded mods.
     */
    private static final List<ModContainer> MODS = new ArrayList<>();

    /**
     * A map of all loaded mods. Key is the mod class and value is the ModContainer.
     */
    private static final Map<Mod, ModContainer> MODS_MAP = new HashMap<>();

    /**
     * A map of all loaded mods. Key is the mod id and value is the ModContainer.
     */
    private static final Map<String, ModContainer> ID_MAP = new HashMap<>();

    /**
     * The running directory for the game.
     */
    private static final File RUN_DIRECTORY = new File(".");

    /**
     * The directory to load mods from.
     */
    private static final File MOD_DIRECTORY = new File(RUN_DIRECTORY, "mods");

    /**
     * Cached immutable versions of the lists and maps used by the loader.
     */
    private static final List<ModContainer> UNM_MODS = Collections.unmodifiableList(MODS);
    private static final Map<Mod, ModContainer> UNM_MODS_MAP = Collections.unmodifiableMap(MODS_MAP);
    private static final Map<String, ModContainer> UNM_ID_MAP = Collections.unmodifiableMap(ID_MAP);

    /**
     * Attempts to load all mods from the mods directory and the classpath. While this is public,
     * it is intended for internal use only!
     * 
     * <br>This is called from {@link OMLTweaker#injectIntoClassLoader(LaunchClassLoader)}}.
     *
     * @throws Exception the exception
     */
    public static void registerMods() throws Exception {
        LoadHandler load = new LoadHandler(MOD_DIRECTORY, true, OpenModLoader.getLogger());
        // if everything is fine, register the mods
        if (load.load()) {
            MODS.addAll(load.getModList());
            ID_MAP.putAll(load.getIdMap());
        }
    }

    /**
     * Iterates through all registered mods and enables them. If there is an
     * issue in registering the mod, it will be disabled.
     * 
     * <br>This is called from {@link OpenModLoader#minecraftConstruction(SidedHandler)}.
     */
    public static void loadMods() {
        // load the instances
        for (ModContainer mod : MODS) {
            // if this is the wrong side, skip the mod
            if (mod.getSide() != Side.UNIVERSAL && mod.getSide() != OpenModLoader.getSidedHandler().getSide()) {
                continue;
            }
            Mod instance = mod.getInstance();
            if (instance != null) {
                MODS_MAP.put(instance, mod);
                // populate @Instance fields
                for (Field field: instance.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(Instance.class)) {
                        try {
                            field.setAccessible(true);
                            if (Modifier.isFinal(field.getModifiers())) {
                                Field modifiersField = Field.class.getDeclaredField("modifiers");
                                modifiersField.setAccessible(true);
                                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                            }
                            field.set(null, instance);
                        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                            OpenModLoader.getLogger().error("Could not set @Instance field", e);
                        }
                    }
                }
            }
        }
        // initialize the mods and start update checker
        for (ModContainer mod : MODS) {
            // if this is the wrong side, skip the mod
            if (mod.getSide() != Side.UNIVERSAL && mod.getSide() != OpenModLoader.getSidedHandler().getSide()) {
                continue;
            }
            Mod instance = mod.getInstance();
            if (instance != null) {
                instance.onInitialize();
            }
            // run the update checker
            if (!UpdateManager.hasUpdateContainer(mod) && mod.getUpdateURL() != null) {
                try {
                    UpdateManager.registerUpdater(mod, new JsonUpdateContainer(new URL(mod.getUpdateURL())));
                } catch (MalformedURLException e) {
                    OpenModLoader.getLogger().catching(e);
                }
            }
        }
    }

    /**
     * Returns an immutable and sorted list of mods.
     *
     * @return an immutable and sorted list of mods
     */
    public static List<ModContainer> getModList() {
        return UNM_MODS;
    }

    /**
     * Returns an immutable map of mod IDs to mod containers.
     *
     * @return an immutable map of mod IDs to mod containers
     */
    public static Map<String, ModContainer> getIndexedModList() {
        return UNM_ID_MAP;
    }

    /**
     * Returns an immutable map of mod objects to mod containers.
     * <br>
     * <b>This map may be smaller than {@link #getModList()} and
     * {@link #getIndexedModList()}. It only contains mods that
     * specify a mod class.
     *
     * @return an immutable map of mod objects to mod containers
     */
    public static Map<Mod, ModContainer> getModInstanceList() {
        return UNM_MODS_MAP;
    }

    /**
     * Get the mod container of a mod.
     *
     * @param mod the mod instance
     * @return the mod container
     */
    public static ModContainer getContainer(Mod mod) {
        return MODS_MAP.get(mod);
    }

    /**
     * Get the mod container of a mod.
     *
     * @param id the mod id
     * @return the mod container
     */
    public static ModContainer getContainer(String id) {
        return ID_MAP.get(id);
    }

    /**
     * Checks if a mod with the specified id is loaded.
     *
     * @param id the id
     * @return true, if the mod is loaded
     */
    public static boolean isModLoaded(String id) {
        return ID_MAP.containsKey(id);
    }
}
