package xyz.openmodloader.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import xyz.openmodloader.modloader.ModContainer;

public class GuiModInfo extends GuiScreen {
    private GuiModList parent;
    private ModContainer container;

    public GuiModInfo(GuiModList parent, ModContainer container) {
        this.parent = parent;
        this.container = container;
    }

    @Override
    public void initGui() {
        this.func_189646_b(new GuiButton(0, this.width / 2 + 4, this.height - 30, 150, 20, I18n.format("gui.done")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        }
    }
}
