package xyz.openmodloader.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.ISidedHandler;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.client.gui.GuiSnackbar;
import xyz.openmodloader.event.impl.MessageEvent;
import xyz.openmodloader.event.impl.UpdateEvent;
import xyz.openmodloader.event.strippable.Side;
import xyz.openmodloader.event.strippable.Strippable;
import xyz.openmodloader.modloader.ModLoader;

import java.util.ArrayList;
import java.util.List;

@Strippable(side = Side.CLIENT)
public enum OMLClientHandler implements ISidedHandler {
    INSTANCE;

    @Override
    public void onInitialize() {
        OpenModLoader.INSTANCE.getEventBus().register(UpdateEvent.ClientUpdate.class, event -> {
            if (GuiSnackbar.CURRENT_SNACKBAR != null) {
                GuiSnackbar.CURRENT_SNACKBAR.updateSnackbar();
            } else if (!GuiSnackbar.SNACKBAR_LIST.isEmpty()) {
                GuiSnackbar.CURRENT_SNACKBAR = GuiSnackbar.SNACKBAR_LIST.get(0);
                GuiSnackbar.SNACKBAR_LIST.remove(GuiSnackbar.CURRENT_SNACKBAR);
            }
        });

        OpenModLoader.INSTANCE.getEventBus().register(UpdateEvent.RenderUpdate.class, event -> {
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
        component = MessageEvent.Snackbar.handle(component, Side.SERVER);
        if (component == null) {
            return;
        }
        GuiSnackbar.SNACKBAR_LIST.add(new GuiSnackbar(component));
    }

    @Override
    public EntityPlayerSP getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public List<String> getMainMenuStrings() {
        List<String> list = new ArrayList<>();
        list.add(ModLoader.MODS.size() + " mod" + ((ModLoader.MODS.size() == 1 ? "" : "s") + " enabled"));
        list.add("Version " + OpenModLoader.INSTANCE.getVersion());
        list.add("OpenModLoader");
        return list;
    }
}
