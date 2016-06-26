package xyz.openmodloader.modloader.version;

import com.google.common.collect.ImmutableList;
import xyz.openmodloader.modloader.ModContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateManager {
    private static final Map<ModContainer, UpdateContainer> UPDATE_CONTAINERS = new HashMap<>();
    private static final Map<ModContainer, UpdateContainer> OUTDATED_MODS = new HashMap<>();

    public static void registerUpdater(ModContainer mod, UpdateContainer container) {
        UPDATE_CONTAINERS.put(mod, container);
    }

    public static List<UpdateContainer> getOutdatedMods() {
        return ImmutableList.copyOf(OUTDATED_MODS.values());
    }

    public static boolean isModOutdated(ModContainer container) {
        return OUTDATED_MODS.containsKey(container);
    }

    public static boolean hasUpdateContainer(ModContainer container) {
        return UPDATE_CONTAINERS.keySet().contains(container);
    }

    public static UpdateContainer getUpdateContainer(ModContainer container) {
        return UPDATE_CONTAINERS.get(container);
    }

    public static void checkForUpdates() {
        OUTDATED_MODS.clear();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (Map.Entry<ModContainer, UpdateContainer> entry : UPDATE_CONTAINERS.entrySet()) {
            executor.execute(() -> {
                if (!entry.getKey().getVersion().atLeast(entry.getValue().getLatestVersion())) {
                    OUTDATED_MODS.put(entry.getKey(), entry.getValue());
                }
            });
        }
        executor.shutdown();
    }
}
