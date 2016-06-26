package xyz.openmodloader.modloader;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import net.minecraft.launchwrapper.Launch;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.client.gui.GuiMissingDependencies;
import xyz.openmodloader.event.impl.GuiEvent;
import xyz.openmodloader.launcher.OMLAccessTransformer;
import xyz.openmodloader.launcher.OMLStrippableTransformer;
import xyz.openmodloader.launcher.strippable.Side;

public class ModLoader {
    /**
     * A list of all loaded mods.
     */
    public static final List<ModContainer> MODS = new ArrayList<>();

    /**
     * A map of all loaded mods. Key is the mod class and value is the ModContainer.
     */
    private static final Map<IMod, ModContainer> MODS_MAP = new HashMap<>();

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
     * Attempts to load all mods from the mods directory. While this is public,
     * it is intended for internal use only!
     */
    public static void loadMods() {
        try {
            ModContainer oml = new OMLModContainer();
            MODS.add(oml);
            ID_MAP.put(oml.getModID(), oml);
            List<ManifestModContainer> unsortedMods = new ArrayList<>();
            if (MOD_DIRECTORY.exists()) {
                File[] files = MOD_DIRECTORY.listFiles();
                if (files != null) {
                    for (File mod : files) {
                        Launch.classLoader.addURL(mod.toURI().toURL());
                    }
                }
            }

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
                            ManifestModContainer mod = loadMod(file, new Manifest(stream));
                            if (mod != null) {
                                if (ID_MAP.containsKey(mod.getModID()))
                                    throw new RuntimeException("Mod ID '" + mod.getModID() + "' has already been registered");
                                unsortedMods.add(mod);
                                ID_MAP.put(mod.getModID(), mod);
                            }
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
            // sort the mods
            MODS.addAll(DependencySorter.sort(unsortedMods));
            List<String> missingDeps = Lists.newArrayList();
            List<String> oudatedDeps = Lists.newArrayList();
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
            if (!missingDeps.isEmpty() || !oudatedDeps.isEmpty()) {
                MODS.clear();
                ID_MAP.clear();
                if (OpenModLoader.INSTANCE.getSidedHandler().getSide() == Side.CLIENT) {
                    OpenModLoader.INSTANCE.getEventBus().register(GuiEvent.Open.class, (e) -> e.setGui(new GuiMissingDependencies(missingDeps, oudatedDeps)));
                }
            }
            // now that we've checked dependencies, it's time to register the coremods
            for (ModContainer mod: MODS)
                for (String transformer : mod.getTransformers()) {
                    Launch.classLoader.registerTransformer(transformer);
                }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attempts to load a mod from an input stream. This will parse the
     * mods.json file.
     *
     * @param manifest the manifest instance
     */
    private static ManifestModContainer loadMod(File file, Manifest manifest) {
        ManifestModContainer container = ManifestModContainer.create(file, manifest);
        if (container == null) {
            OpenModLoader.INSTANCE.getLogger().error("Found invalid manifest in file " + file.getAbsolutePath().replace("!", "").replace(File.separator + "META-INF" + File.separator + "MANIFEST.MF", ""));
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
        if (!container.getMinecraftVersion().equals(OpenModLoader.INSTANCE.getMinecraftVersion())) {
            OpenModLoader.INSTANCE.getLogger().warn("Mod '%s' is expecting Minecraft %s, but we are running on Minecraft %s!", container.getName(), container.getMinecraftVersion(), OpenModLoader.INSTANCE.getMinecraftVersion());
        }
        if (container.getSide() != Side.UNIVERSAL && container.getSide() != OMLStrippableTransformer.getSide()) {
            OpenModLoader.INSTANCE.getLogger().info("Invalid side %s for mod %s. The mod will not be loaded.", OMLStrippableTransformer.getSide(), container.getName());
            return container;
        }
        return container;
    }

    /**
     * Iterates through all registered mods and enables them. If there is an
     * issue in registering the mod, it will be disabled.
     */
    public static void registerMods() {
        for (ModContainer mod : MODS) {
            if (mod.getSide() != Side.UNIVERSAL && mod.getSide() != OpenModLoader.INSTANCE.getSidedHandler().getSide()) {
                continue;
            }
            IMod instance = mod.getInstance();
            if (instance != null) {
                MODS_MAP.put(instance, mod); // load the instances
            }
        }
        for (ModContainer mod : MODS) {
            try {
                if (mod.getSide() != Side.UNIVERSAL && mod.getSide() != OpenModLoader.INSTANCE.getSidedHandler().getSide()) {
                    continue;
                }
                IMod instance = mod.getInstance();
                if (instance != null) {
                    instance.onEnable();
                }
            } catch (RuntimeException e) {
                OpenModLoader.INSTANCE.getLogger().warn("An error occurred while enabling mod " + mod.getModID());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get the mod container of a mod.
     *
     * @param mod the mod instance
     * @return the mod container
     */
    public static ModContainer getContainer(IMod mod) {
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
}
