package xyz.openmodloader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import xyz.openmodloader.event.strippable.Side;
import xyz.openmodloader.event.strippable.Strippable;
import xyz.openmodloader.modloader.ModContainer;
import xyz.openmodloader.modloader.ModLoader;

import java.awt.*;

@Strippable(side = Side.CLIENT)
public class GuiOML extends GuiScreen {

    protected GuiScreen prevScreen;
	private GuiButton backButton;

    public GuiOML(GuiScreen prevScreen) {
        this.prevScreen = prevScreen;
    }

    @Override
    public void initGui() {
        this.backButton = new GuiButton(0, this.width / 2 - 100, this.height - 28, 200, 20, I18n.format("gui.cancel"));
        this.buttonList.add(backButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
		if (button == this.backButton) {
			Minecraft.getMinecraft().displayGuiScreen(prevScreen);
		}
    }

    @Override
    public void drawScreen(int mousex, int mousey, float ticks) {
        drawBackground(0);
        super.drawScreen(mousex, mousey, ticks);
        this.drawCenteredString(this.fontRendererObj, "Open Mod Loader Settings", this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, ModLoader.MODS.size() + " loaded mod" + (ModLoader.MODS.size() > 1 ? "s" : "") + ":", 10, 50, Color.gray.getRGB());
        int i = 0;
        for (ModContainer mod : ModLoader.MODS) {
            i++;
            this.drawString(this.fontRendererObj, mod.getName(), 15, 70 * i, Color.LIGHT_GRAY.getRGB());
        }
    }
}