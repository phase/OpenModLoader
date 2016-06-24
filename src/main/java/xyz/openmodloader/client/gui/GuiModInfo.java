package xyz.openmodloader.client.gui;

import net.minecraft.client.gui.GuiScreen;
import xyz.openmodloader.modloader.ModContainer;

public class GuiModInfo extends GuiScreen {
    private GuiModList parent;
    private ModContainer container;

    public GuiModInfo(GuiModList parent, ModContainer container) {
        this.parent = parent;
        this.container = container;
    }
}
