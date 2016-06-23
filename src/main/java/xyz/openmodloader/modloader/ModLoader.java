package xyz.openmodloader.modloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.launchwrapper.LaunchClassLoader;
import xyz.openmodloader.OpenModLoader;

public final class ModLoader {

    /**
     * A map of all loaded mods. Key is the ID and value is the ModContainer.
     */
    public static final Map<String, ModContainer> MODS = new HashMap<String, ModContainer>();

    /**
     * The running directory for the game.
     */
    private static final File RUN_DIRECTORY = new File(".");

    /**
     * The directory to load mods from.
     */
    private static final File MOD_DIRECTORY = new File(RUN_DIRECTORY, "mods");

    /**
     * GSON instance used by loader to read mod.json file.
     */
    private static final Gson GSON = new Gson();

    /**
     * Json parser instance used by loader to read mod.json file.
     */
    private static final JsonParser PARSER = new JsonParser();

    /**
     * Attempts to load all mods from the mods directory. While this is public,
     * it is intended for internal use only!
     */
    public static void loadMods() {
        try {
            if (MOD_DIRECTORY.exists()) {
                File[] files = MOD_DIRECTORY.listFiles();
                if (files != null) {
                    for (File mod : files) {
                        ((LaunchClassLoader) ModLoader.class.getClassLoader()).addURL(mod.toURI().toURL());
                    }
                }
            }

            URL roots = ModLoader.class.getClassLoader().getResource("");
            if (roots != null) {
                File root = new File(roots.getPath());
                File[] files = root.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().equals("mod.json")) {
                            final FileInputStream stream = new FileInputStream(file);
                            loadMod(stream);
                            stream.close();
                        }
                        else if (file.getName().endsWith(".at")) {
                    	    Multimap<String, String> entries = OMLAccessTransformer.getEntries();
                    	    for (String line: FileUtils.readLines(file)) {
                    	    	if (line.matches("\\w+((\\.\\w+)+|)\\s+\\w+(\\(\\S+|)")) {
                    	    		String[] parts = line.split(" ");
                    	    		entries.put(parts[0], parts[1]);
                    	    	}
                    	    }
                        }
                    }
                }
            }
        } catch (Exception e) {
            OpenModLoader.INSTANCE.LOGGER.warn(e);
        }
    }

    /**
     * Attempts to load a mod from an input stream. This will parse the
     * mods.json file and add the mod to {@link #MODS}.
     * 
     * @param stream The input stream to read the mod data from.
     */
    private static void loadMod(InputStream stream) {
        List<ModContainer> containerList = new ArrayList<>();
        JsonElement element = PARSER.parse(new InputStreamReader(stream));
        if (element.isJsonArray()) {
            for (JsonElement e : element.getAsJsonArray()) {
                containerList.add(GSON.fromJson(e, ModContainer.class));
            }
        } else {
            containerList.add(GSON.fromJson(element, ModContainer.class));
        }
        containerList.stream().filter(container -> container != null).forEach(container -> {
            OpenModLoader.INSTANCE.LOGGER.info("Found mod " + container.getName() + " (with id " + container.getModID() + ")");
            MODS.put(container.getModID(), container);
        });
    }

    /**
     * Iterates through all registered mods and enables them. If there is an
     * issue in registering the mod, it will be disabled.
     */
    public static void registerMods() {
        for (ModContainer mod : MODS.values()) {
            try {
                mod.getInstance().onEnable();
            } catch (RuntimeException e) {
                OpenModLoader.INSTANCE.LOGGER.warn("An error occurred while enabling mod " + mod.getModID());
                OpenModLoader.INSTANCE.LOGGER.warn(e);
                MODS.remove(mod.getModID());
            }
        }
    }
}
