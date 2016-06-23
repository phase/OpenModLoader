package xyz.openmodloader.launcher;

import com.google.common.base.Strings;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.util.*;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.strippable.Side;

public class OpenModLoaderClient {
    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../.gradle/minecraft/natives/").getAbsolutePath());
        OMLSideTransformer.SIDE = Side.CLIENT;
        Launch.main(getArgs(args));
    }

    private static String[] getArgs(String[] args) {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("version", "1.10");
        argMap.put("accessToken", "OpenModLoader");
        argMap.put("tweakClass", "xyz.openmodloader.launcher.OMLTweaker");

        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : argMap.entrySet()) {
            String x = entry.getValue();
            if (!Strings.isNullOrEmpty(x)) {
                list.add("--" + entry.getKey());
                list.add(x);
            }
        }
        Collections.addAll(list, args);
        return list.toArray(new String[list.size()]);
    }
}
