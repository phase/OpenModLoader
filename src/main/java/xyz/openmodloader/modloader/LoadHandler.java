package xyz.openmodloader.modloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharSet;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import net.minecraft.launchwrapper.Launch;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.launcher.OMLAccessTransformer;
import xyz.openmodloader.launcher.OMLStrippableTransformer;
import xyz.openmodloader.launcher.strippable.Environment;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.modloader.version.Version;
import xyz.openmodloader.util.InternalUtils;

class LoadHandler {

    private final Map<String, ModContainer> idMap = new HashMap<>();
    private List<ModContainer> modList = new ArrayList<>();

    private final Map<String, Version> missingDeps = new HashMap<>();
    private final Map<ModContainer, Version> outdatedDeps = new HashMap<>();
    private final Multimap<String, ModContainer> duplicates = Multimaps.newSetMultimap(new HashMap<>(), () -> new HashSet<>());

    private final File modsDirectory;
    private final boolean searchClassPath;
    private final Logger log;

    public LoadHandler(File modsDirectory, boolean searchClassPath, Logger log) {
        this.modsDirectory = modsDirectory;
        this.searchClassPath = searchClassPath;
        this.log = log;
    }

    /**
     * Searches the mod directory and the classpath for mods, and registers
     * all applicable files. Also responsible for mod sorting, duplication
     * checks and dependency checks.
     *
     * @return true, if no errors were found, and loading can continue
     */
    public boolean load() throws Exception {
        // if a mod directory has been set, search it for mods
        if (modsDirectory != null) {
            searchDirectory();
        }
        // search the classpath for mods, if applicable
        if (searchClassPath) {
            searchClassPath();
        }
        // sort the mods
        sort();
        // add OML to the beginning of the mod list
        ModContainer oml = new OMLModContainer();
        modList.add(0, oml);
        idMap.put(oml.getModID(), oml);
        // check dependencies and duplicates
        if (checkDependencies() && duplicates.isEmpty()) {
            // if everything is okay, load the mod transformers and return true
            for (ModContainer mod: modList) {
                for (String transformer : mod.getTransformers()) {
                    Launch.classLoader.registerTransformer(transformer);
                }
            }
            return true;
        } else {
            // if anything is wrong, show a GUI (client) or throw an exception (server), and return false
            if (OMLStrippableTransformer.getSide() == Side.CLIENT) {
                List<String> missingDeps = new ArrayList<>();
                for (Entry<String, Version> e: this.missingDeps.entrySet()) {
                    if (e.getValue().getMajor() == 0 && e.getValue().getMinor() == 0 && e.getValue().getPatch() == 0)
                        missingDeps.add(e.getKey());
                    else
                        missingDeps.add(e.getKey() + " v" + e.getValue() + " or higher");
                }
                List<String> outdatedDeps = new ArrayList<>();
                for (Entry<ModContainer, Version> e: this.outdatedDeps.entrySet()) {
                    outdatedDeps.add(e.getKey().getName() + " v" + e.getKey().getVersion() + " (requires v" + e.getValue() + " or higher)");
                }
                List<String> duplicates = new ArrayList<>();
                for (String modid: this.duplicates.keySet()) {
                    StringBuilder b = new StringBuilder();
                    b.append(modid).append(": ");
                    for (ModContainer mod: this.duplicates.get(modid)) {
                        b.append(mod.getModFile().getName()).append(", ");
                    }
                    duplicates.add(b.substring(0, b.length() - 2));
                }
                // something weird is going on, see the comment for InternalUtils.openErrorGui()
                InternalUtils.openErrorGui(missingDeps, outdatedDeps, duplicates);
            } else {
                throw new RuntimeException("Errors during load - see log for more information");
            }
            return false;
        }
    }

    public List<ModContainer> getModList() {
        return modList;
    }

    public Map<String, ModContainer> getIdMap() {
        return idMap;
    }

    private boolean checkDependencies() {
        for (ModContainer mod : modList) {
            for (String dep : mod.getDependencies()) {
                String[] depParts = dep.split("\\s*:\\s*");
                if (depParts[0].startsWith("optional "))
                    continue;
                ModContainer depContainer = idMap.get(depParts[0]);
                Version min = depParts.length > 1 ? new Version(depParts[1]) : new Version(0, 0, 0);
                if (depContainer == null) {
                    Version v = missingDeps.get(depParts[0]);
                    if (v == null || !v.atLeast(min)) {
                        missingDeps.put(depParts[0], min);
                    }
                    OpenModLoader.getLogger().error("Missing dependency '%s' for mod '%s'.", depParts[0], mod.getName());
                } else if (depParts.length > 1 && !depContainer.getVersion().atLeast(min)) {
                    Version v = outdatedDeps.get(depParts[0]);
                    if (v == null || !v.atLeast(min)) {
                        outdatedDeps.put(depContainer, min);
                    }
                    OpenModLoader.getLogger().error("Outdated dependency '%s' for mod '%s'. Expected version '%s', but got version '%s'.", depContainer.getName(), mod.getName(), depParts[1], depContainer.getVersion());
                }
            }
        }
        return missingDeps.isEmpty() && outdatedDeps.isEmpty();
    }

    /**
     * Registers mods from the mods folder.
     *
     * @param duplicates the duplicates
     * @param modList the unsorted mods
     * @throws Exception the exception
     */
    private void searchDirectory() throws Exception {
        if (modsDirectory.exists()) {
            File[] files = modsDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".jar") && !file.isDirectory()) {
                        Launch.classLoader.addURL(file.toURI().toURL());
                        JarFile jar = new JarFile(file);
                        Manifest manifest = jar.getManifest();
                        if (manifest != null && manifest.getMainAttributes().containsKey(new Attributes.Name("ID"))) {
                            ManifestModContainer mod = registerMod(file, manifest);
                            // load the logo (oh I hate this code...)
                            if (mod.getLogo() != null) {
                                try (InputStream in = jar.getInputStream(jar.getJarEntry(mod.getLogo()))) {
                                    mod.setLogo(IOUtils.toByteArray(in));
                                }
                            }
                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry e = entries.nextElement();
                                if (e.getName().endsWith(".at")) {
                                    InputStream in = jar.getInputStream(e);
                                    OMLAccessTransformer.loadAccessTransformers(IOUtils.readLines(in));
                                    in.close();
                                }
                            }
                            jar.close();
                        } else {
                            log.debug("Found non-mod jar file '%s'. The file will be loaded as a library.", file.getName());
                        }
                    } else if (file.isDirectory()) {
                        log.warn("Found directory '%s' in the mods folder. The mods folder should *only* contain jar files, not directories or other files.", file.getName());
                    } else {
                        log.warn("Found non-jar file in the mods folder. The mods folder should *only* contain jar files. Please move the file.");
                    }
                }
            }
        } else {
            modsDirectory.mkdirs();
        }
    }

    /**
     * Registers the class path mods.
     *
     * @param duplicates the duplicates
     * @param modList the unsorted mods
     * @throws Exception the exception
     */
    private void searchClassPath() throws Exception {
        URL roots;
        Enumeration<URL> metas = Launch.classLoader.getResources("META-INF");
        while (metas.hasMoreElements()) {
            roots = metas.nextElement();
            File root = new File(roots.getPath());
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().equals("MANIFEST.MF") || (OMLStrippableTransformer.getEnvironment() == Environment.DEVELOPMENT && file.getName().endsWith(".MF"))) {
                        FileInputStream stream = new FileInputStream(file);
                        registerMod(file.getParentFile().getParentFile(), new Manifest(stream));
                        stream.close();
                    } else if (file.getName().endsWith(".at")) {
                        OMLAccessTransformer.loadAccessTransformers(FileUtils.readLines(file));
                    }
                }
            }
        }
    }

    /**
     * Constructs and registers a mod.
     *
     * @param duplicates the duplicates
     * @param modList the unsorted mods
     * @param file the file
     * @param manifest the manifest
     */
    private ManifestModContainer registerMod(File file, Manifest manifest) {
        ManifestModContainer mod = loadMod(file, manifest);
        if (mod != null) {
            if (idMap.containsKey(mod.getModID())) {
                ModContainer mod2 = idMap.get(mod.getModID());
                duplicates.put(mod.getModID(), mod);
                duplicates.put(mod.getModID(), mod2);
                log.error("Duplicate mod IDs for files '%s' and '%s'", mod.getModFile(), mod2.getModFile());
            } else {
                modList.add(mod);
                idMap.put(mod.getModID(), mod);
            }
        }
        return mod;
    }

    /**
     * Attempts to load a mod from an input stream. This will parse the
     * manifest file.
     *
     * @param file the file
     * @param manifest the manifest instance
     * @return the manifest mod container
     */
    private ManifestModContainer loadMod(File file, Manifest manifest) {
        ManifestModContainer container = ManifestModContainer.create(file, manifest);
        if (container == null) {
            log.error("Found invalid manifest in file " + file);
            return null;
        }
        log.info("Found mod " + container.getName() + " with id " + container.getModID());
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
        if (!container.getMinecraftVersion().equals(OpenModLoader.getMinecraftVersion())) {
            log.warn("Mod '%s' is expecting Minecraft %s, but we are running on Minecraft %s!", container.getName(), container.getMinecraftVersion(), OpenModLoader.getMinecraftVersion());
        }
        if (container.getSide() != Side.UNIVERSAL && container.getSide() != OMLStrippableTransformer.getSide()) {
            log.info("Invalid side %s for mod %s. The mod will not be loaded.", OMLStrippableTransformer.getSide(), container.getName());
        }
        return container;
    }

    private void sort() {
        LinkedList<ModContainer> sorted = Lists.newLinkedList();
        for (ModContainer mod: modList) {
            if (sorted.isEmpty() || mod.getDependencies().length == 0)
                sorted.addFirst(mod);
            else {
                boolean b = false;
                l1:
                for (int i = 0; i < sorted.size(); i++)
                    for (String dep: sorted.get(i).getDependencies()) {
                        if (dep.split("\\s:\\s")[0].equals(mod.getModID())) {
                            sorted.add(i, mod);
                            b = true;
                            break l1;
                        }
                    }
                if (!b)
                    sorted.addLast(mod);
            }
        }
        modList = sorted;
    }
}