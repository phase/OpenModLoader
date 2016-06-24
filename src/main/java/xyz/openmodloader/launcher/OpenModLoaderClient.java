package xyz.openmodloader.launcher;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.net.Proxy;
import java.util.*;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.strippable.Side;

public class OpenModLoaderClient {
    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../.gradle/minecraft/natives/").getAbsolutePath());
        OMLSideTransformer.SIDE = Side.CLIENT;
        Map<String, String> argMap = parseArgs(args);
        if (argMap.containsKey("password")) {
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) (new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1")).createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(argMap.get("username"));
            auth.setPassword(argMap.get("password"));
            argMap.remove("password");

            try {
                auth.logIn();
            } catch (AuthenticationException e) {
                e.printStackTrace();
                return;
            }

            argMap.put("accessToken", auth.getAuthenticatedToken());
            argMap.put("uuid", auth.getSelectedProfile().getId().toString().replace("-", ""));
            argMap.put("username", auth.getSelectedProfile().getName());
            argMap.put("userType", auth.getUserType().getName());
            argMap.put("userProperties", new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create().toJson(auth.getUserProperties()));
        }
        Launch.main(getArgs(argMap));
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> argMap = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                argMap.put(args[i].substring(2, args[i].length()), args[i + 1]);
            }
        }
        return argMap;
    }

    private static String[] getArgs(Map<String, String> argMap) {
        argMap.put("version", "1.10");
        argMap.put("assetIndex", "1.10");
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
        return list.toArray(new String[list.size()]);
    }
}
