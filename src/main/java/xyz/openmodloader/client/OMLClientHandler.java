package xyz.openmodloader.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.SidedHandler;
import xyz.openmodloader.client.gui.GuiSnackbar;
import xyz.openmodloader.event.impl.MessageEvent;
import xyz.openmodloader.event.impl.UpdateEvent;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;
import xyz.openmodloader.modloader.ModContainer;
import xyz.openmodloader.modloader.ModLoader;
import xyz.openmodloader.util.ReflectionHelper;

@Strippable(side = Side.CLIENT)
public class OMLClientHandler implements SidedHandler {
    @Override
    public void onInitialize() {
        List<IResourcePack> modResourcePacks = new ArrayList<>();
        for (ModContainer mod : ModLoader.getModList()) {
            File file = mod.getModFile();
            if (file.isDirectory()) {
                modResourcePacks.add(new OMLFolderResourcePack(mod));
            } else {
                modResourcePacks.add(new OMLFileResourcePack(mod));
            }
        }
        try {
            List<IResourcePack> defaultResourcePacks = ReflectionHelper.getValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "aD");
            defaultResourcePacks.addAll(modResourcePacks);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        OpenModLoader.getEventBus().register(UpdateEvent.ClientUpdate.class, event -> {
            if (GuiSnackbar.CURRENT_SNACKBAR != null) {
                GuiSnackbar.CURRENT_SNACKBAR.updateSnackbar();
            } else if (!GuiSnackbar.SNACKBAR_LIST.isEmpty()) {
                GuiSnackbar.CURRENT_SNACKBAR = GuiSnackbar.SNACKBAR_LIST.get(0);
                GuiSnackbar.SNACKBAR_LIST.remove(GuiSnackbar.CURRENT_SNACKBAR);
            }
        });

        OpenModLoader.getEventBus().register(UpdateEvent.RenderUpdate.class, event -> {
            if (GuiSnackbar.CURRENT_SNACKBAR != null) {
                GuiSnackbar.CURRENT_SNACKBAR.drawSnackbar();
            }
        });
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void openSnackbar(ITextComponent component) {
        component = MessageEvent.Snackbar.handle(component, Side.CLIENT);
        if (component == null) {
            return;
        }
        GuiSnackbar.SNACKBAR_LIST.add(new GuiSnackbar(component));
    }

    @Override
    public EntityPlayerSP getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public void handleOnMainThread(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }

    @Override
    public MinecraftServer getServer() {
        return Minecraft.getMinecraft().getIntegratedServer();
    }

    public static List<String> getMainMenuStrings() {
        List<String> list = new ArrayList<>();
        list.add(ModLoader.getModList().size() + " mod" + ((ModLoader.getModList().size() == 1 ? "" : "s") + " enabled"));
        list.add("Version " + OpenModLoader.getVersion());
        list.add("OpenModLoader");
        return list;
    }
}
