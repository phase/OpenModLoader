package xyz.openmodloader.modloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharSet;

import com.google.common.collect.Multimap;

import net.minecraft.launchwrapper.Launch;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.client.gui.GuiLoadError;
import xyz.openmodloader.event.impl.GuiEvent;
import xyz.openmodloader.launcher.OMLAccessTransformer;
import xyz.openmodloader.launcher.OMLStrippableTransformer;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.modloader.version.JsonUpdateContainer;
import xyz.openmodloader.modloader.version.UpdateManager;
import xyz.openmodloader.modloader.version.Version;

/**
 * The Class ModLoader.
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

    private static final List<ModContainer> UNM_MODS = Collections.unmodifiableList(MODS);
    private static final Map<Mod, ModContainer> UNM_MODS_MAP = Collections.unmodifiableMap(MODS_MAP);
    private static final Map<String, ModContainer> UNM_ID_MAP = Collections.unmodifiableMap(ID_MAP);

    /**
     * Attempts to load all mods from the mods directory. While this is public,
     * it is intended for internal use only!
     *
     * @throws Exception the exception
     */
    public static void registerMods() throws Exception {
        // register the OML container
        ModContainer oml = new OMLModContainer();
        MODS.add(oml);
        ID_MAP.put(oml.getModID(), oml);

        List<ManifestModContainer> unsortedMods = new ArrayList<>();
        Set<String> duplicates = new HashSet<>();

        // register the mods
        registerModsFromFolder(duplicates, unsortedMods);
        registerClassPathMods(duplicates, unsortedMods);

        // sort the mods
        MODS.addAll(DependencySorter.sort(unsortedMods));
        Set<String> missingDeps = new HashSet<>();
        Set<String> oudatedDeps = new HashSet<>();
        // check dependencies
        for (ModContainer mod : MODS) {
            for (String dep : mod.getDependencies()) {
                String[] depParts = dep.split("\\s*:\\s*");
                if (depParts[0].startsWith("optional "))
                    continue;
                ModContainer depContainer = ID_MAP.get(depParts[0]);
                if (depContainer == null) {
                    missingDeps.add(depParts.length > 1 ? depParts[0] + " v" + depParts[1] : depParts[0]);
                    OpenModLoader.INSTANCE.getLogger().error("Missing dependency '%s' for mod '%s'.", depParts[0], mod.getName());
                } else if (depParts.length > 1 && !depContainer.getVersion().atLeast(new Version(depParts[1]))) {
                    oudatedDeps.add(depParts.length > 1 ? depParts[0] + " v" + depParts[1] : depParts[0]);
                    OpenModLoader.INSTANCE.getLogger().error("Outdated dependency '%s' for mod '%s'. Expected version '%s', but got version '%s'.", depContainer.getName(), mod.getName(), depParts[1], depContainer.getVersion());
                }
            }
        }
        // if any dependencies are missing, stop mod loading, and display a GUI
        if (!duplicates.isEmpty() || !missingDeps.isEmpty() || !oudatedDeps.isEmpty()) {
            MODS.clear();
            ID_MAP.clear();
            if (OMLStrippableTransformer.getSide() == Side.CLIENT) {
                OpenModLoader.INSTANCE.getEventBus().register(GuiEvent.Open.class, (e) -> e.setGui(new GuiLoadError(missingDeps, oudatedDeps, duplicates)));
            } else {
                throw new RuntimeException("Errors during load - see log for more information");
            }
        }
        // now that we've checked dependencies, it's time to register the coremods
        for (ModContainer mod: MODS) {
            for (String transformer : mod.getTransformers()) {
                Launch.classLoader.registerTransformer(transformer);
            }
        }
    }

    /**
     * Registers mods from the mods folder.
     *
     * @param duplicates the duplicates
     * @param unsortedMods the unsorted mods
     * @throws Exception the exception
     */
    private static void registerModsFromFolder(Set<String> duplicates, List<ManifestModContainer> unsortedMods) throws Exception {
        if (MOD_DIRECTORY.exists()) {
            File[] files = MOD_DIRECTORY.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".jar") && !file.isDirectory()) {
                        Launch.classLoader.addURL(file.toURI().toURL());
                        JarFile jar = new JarFile(file);
                        if (jar.getManifest() != null) {
                            registerMod(duplicates, unsortedMods, file, jar.getManifest());
                        }
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry e = entries.nextElement();
                            if (e.getName().endsWith(".at")) {
                                System.out.println(e.getName());
                                Multimap<String, String> ats = OMLAccessTransformer.getEntries();
                                IOUtils.readLines(jar.getInputStream(e)).stream().filter(line -> line.matches("\\w+((\\.\\w+)+|)\\s+(\\w+(\\(\\S+|)|\\*\\(\\)|\\*)")).forEach(line -> {
                                    String[] parts = line.split(" ");
                                    ats.put(parts[0], parts[1]);
                                });
                            }
                        }
                        jar.close();
                    }
                }
            }
        } else {
            MOD_DIRECTORY.mkdirs();
        }
    }

    /**
     * Registers the class path mods.
     *
     * @param duplicates the duplicates
     * @param unsortedMods the unsorted mods
     * @throws Exception the exception
     */
    private static void registerClassPathMods(Set<String> duplicates, List<ManifestModContainer> unsortedMods) throws Exception {
        URL roots;
        Enumeration<URL> metas = Launch.classLoader.getResources("META-INF");
        while (metas.hasMoreElements()) {
            roots = metas.nextElement();
            File root = new File(roots.getPath());
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().equals("MANIFEST.MF")) {
                        FileInputStream stream = new FileInputStream(file);
                        registerMod(duplicates, unsortedMods, file.getParentFile().getParentFile(), new Manifest(stream));
                        stream.close();
                    } else if (file.getName().endsWith(".at")) {
                        Multimap<String, String> entries = OMLAccessTransformer.getEntries();
                        FileUtils.readLines(file).stream().filter(line -> line.matches("\\w+((\\.\\w+)+|)\\s+(\\w+(\\(\\S+|)|\\*\\(\\)|\\*)")).forEach(line -> {
                            String[] parts = line.split(" ");
                            entries.put(parts[0], parts[1]);
                        });
                    }
                }
            }
        }
    }

    /**
     * Registers a mod.
     *
     * @param duplicates the duplicates
     * @param unsortedMods the unsorted mods
     * @param file the file
     * @param manifest the manifest
     */
    private static void registerMod(Set<String> duplicates, List<ManifestModContainer> unsortedMods, File file, Manifest manifest) {
        ManifestModContainer mod = loadMod(file, manifest);
        if (mod != null) {
            if (ID_MAP.containsKey(mod.getModID())) {
                ModContainer mod2 = ID_MAP.get(mod.getModID());
                duplicates.add(mod2.getName() + " v" + mod2.getVersion() + " (" + mod2.getModID() + " from " + mod2.getModFile().getName() + ")");
                duplicates.add(mod.getName() + " v" + mod.getVersion() + " (" + mod.getModID() + " from " + mod.getModFile().getName() + ")");
                OpenModLoader.INSTANCE.getLogger().error("Duplicate mod IDs for files '%s' and '%s'", mod.getModFile(), mod2.getModFile());
            } else {
                unsortedMods.add(mod);
                ID_MAP.put(mod.getModID(), mod);
            }
        }
    }

    /**
     * Attempts to load a mod from an input stream. This will parse the
     * manifest file.
     *
     * @param file the file
     * @param manifest the manifest instance
     * @return the manifest mod container
     */
    private static ManifestModContainer loadMod(File file, Manifest manifest) {
        ManifestModContainer container = ManifestModContainer.create(file, manifest);
        if (container == null) {
            OpenModLoader.INSTANCE.getLogger().error("Found invalid manifest in file " + file);
            return null;
        }
        OpenModLoader.INSTANCE.getLogger().info("Found mod " + container.getName() + " with id " + container.getModID());
        if (container.getModID().isEmpty()) {
            throw new RuntimeException("Empty mod ID for mod '" + container.getName() + "'!");
        }
        for (char c : container.getModID().toCharArray()) {
            if (c != '-' && c != '_' && !CharSet.ASCII_ALPHA_LOWER.contains(c) && !CharSet.ASCII_NUMERIC.contains(c)) {
                throw new RuntimeException("Illegal characters in ID '" + container.getModID() + "' for mod '" + container.getName() + "'.");
            }
        }
        if (container.getModID().equals("oml")) {
            throw new RuntimeException("'oml' is a reserved mod id!");
        }
        if (!container.getMinecraftVersion().equals(OpenModLoader.INSTANCE.getMinecraftVersion())) {
            OpenModLoader.INSTANCE.getLogger().warn("Mod '%s' is expecting Minecraft %s, but we are running on Minecraft %s!", container.getName(), container.getMinecraftVersion(), OpenModLoader.INSTANCE.getMinecraftVersion());
        }
        if (container.getSide() != Side.UNIVERSAL && container.getSide() != OMLStrippableTransformer.getSide()) {
            OpenModLoader.INSTANCE.getLogger().info("Invalid side %s for mod %s. The mod will not be loaded.", OMLStrippableTransformer.getSide(), container.getName());
        }
        return container;
    }

    /**
     * Iterates through all registered mods and enables them. If there is an
     * issue in registering the mod, it will be disabled.
     */
    public static void loadMods() {
        for (ModContainer mod : MODS) {
            if (mod.getSide() != Side.UNIVERSAL && mod.getSide() != OpenModLoader.INSTANCE.getSidedHandler().getSide()) {
                continue;
            }
            Mod instance = mod.getInstance();
            if (instance != null) {
                MODS_MAP.put(instance, mod); // load the instances
            }
        }
        for (ModContainer mod : MODS) {
            try {
                if (mod.getSide() != Side.UNIVERSAL && mod.getSide() != OpenModLoader.INSTANCE.getSidedHandler().getSide()) {
                    continue;
                }
                Mod instance = mod.getInstance();
                if (instance != null) {
                    instance.onInitialize();
                }
                if (!UpdateManager.hasUpdateContainer(mod) && mod.getUpdateURL() != null) {
                    InputStream stream = null;
                    try {
                        stream = new URL(mod.getUpdateURL()).openStream();
                        UpdateManager.registerUpdater(mod, new JsonUpdateContainer(stream));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (RuntimeException e) {
                OpenModLoader.INSTANCE.getLogger().warn("An error occurred while enabling mod " + mod.getModID());
                throw new RuntimeException(e);
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
