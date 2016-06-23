package xyz.openmodloader.client;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.strippable.Side;
import xyz.openmodloader.event.strippable.Strippable;
import xyz.openmodloader.modloader.ModLoader;

import java.util.ArrayList;
import java.util.List;

@Strippable(side = Side.CLIENT)
public class OpenModLoaderClient {
    public static List<String> getMainMenuStrings() {
        List<String> list = new ArrayList<>();
        list.add(ModLoader.MODS.size() + " mod" + ((ModLoader.MODS.size() == 1 ? "" : "s") + " enabled"));
        list.add("Version " + OpenModLoader.INSTANCE.getVersion());
        list.add("Open Mod Loader");
        return list;
    }
}
