package xyz.openmodloader.launcher;

import net.minecraft.launchwrapper.Launch;
import xyz.openmodloader.launcher.strippable.Side;

public class OpenModLoaderServer {
    public static void main(String[] args) {
        OMLStrippableTransformer.SIDE = Side.SERVER;
        LaunchArguments arguments = new LaunchArguments(args);

        arguments.addArgument("version", "1.10.2");
        arguments.addArgument("assetIndex", "1.10");
        arguments.addArgument("accessToken", "OpenModLoader");
        arguments.addArgument("tweakClass", "xyz.openmodloader.launcher.OMLServerTweaker");

        Launch.main(arguments.getArguments());
    }
}
