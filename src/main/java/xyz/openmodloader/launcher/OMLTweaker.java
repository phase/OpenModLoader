package xyz.openmodloader.launcher;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.main.Main;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import xyz.openmodloader.modloader.ModLoader;

public class OMLTweaker implements ITweaker {
    private Map<String, String> args;

    @SuppressWarnings("unchecked")
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args = (Map<String, String>) Launch.blackboard.get("launchArgs");
        if (this.args == null) {
            this.args = new HashMap<>();
            Launch.blackboard.put("launchArgs", this.args);
        }
        if (!this.args.containsKey("--version")) {
            this.args.put("--version", profile != null ? profile : "OML");
        }
        if (!this.args.containsKey("--gameDir") && gameDir != null) {
            this.args.put("--gameDir", gameDir.getAbsolutePath());
        }
        if (!this.args.containsKey("--assetsDir") && assetsDir != null) {
            this.args.put("--assetsDir", assetsDir.getAbsolutePath());
        }
        if (!this.args.containsKey("--accessToken")) {
            this.args.put("--accessToken", "OpenModLoader");
        }
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (arg.startsWith("--")) {
                this.args.put(arg, args.get(i + 1));
            }
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer(OMLClassTransformer.class.getCanonicalName());
        classLoader.registerTransformer(OMLSideTransformer.class.getCanonicalName());
        classLoader.registerTransformer(OMLAccessTransformer.class.getCanonicalName());
        classLoader.addClassLoaderExclusion("com.google.common");
        classLoader.addTransformerExclusion("xyz.openmodloader.modloader");
        classLoader.addTransformerExclusion("xyz.openmodloader.launcher");
        try {
            Class.forName("xyz.openmodloader.modloader.ModLoader", true, classLoader).getMethod("loadMods").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLaunchTarget() {
        return Main.class.getCanonicalName();
    }

    @Override
    public String[] getLaunchArguments() {
        List<String> launchArgs = new ArrayList<>();
        for (Map.Entry<String, String> arg : this.args.entrySet()) {
            launchArgs.add(arg.getKey());
            launchArgs.add(arg.getValue());
        }
        return launchArgs.toArray(new String[launchArgs.size()]);
    }
}
