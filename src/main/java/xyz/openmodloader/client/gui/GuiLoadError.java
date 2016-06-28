package xyz.openmodloader.client.gui;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;

@Strippable(side = Side.CLIENT)
public class GuiLoadError extends GuiScreen {

    private final Collection<?> missing;
    private final Collection<?> outdated;
    private final Collection<?> duplicates;

    public GuiLoadError(Collection<?> missing, Collection<?> oudated, Collection<?> duplicates) {
        this.missing = missing;
        this.outdated = oudated;
        this.duplicates = duplicates;
    }

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 30, "Exit"));
    }

    @Override
    public void drawScreen(int var1, int var2, float var3) {
        this.drawDefaultBackground();
        int i = 0;
        this.drawCenteredString(this.fontRendererObj, "There were several critical errors during load.", this.width / 2, i += 13, 16777215);
        this.drawCenteredString(this.fontRendererObj, "More information may be found in the log.", this.width / 2, i += 13, 16777215);
        if (!missing.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, "Missing Dependencies:", this.width / 2, i += 20, 16777215);
            for (Object dep: missing) {
                this.drawCenteredString(this.fontRendererObj, String.valueOf(dep), this.width / 2, i += 13, 16777215);
            }
        }
        if (!outdated.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, "Outdated Dependencies:", this.width / 2, i += 20, 16777215);
            for (Object dep: outdated) {
                this.drawCenteredString(this.fontRendererObj, String.valueOf(dep), this.width / 2, i += 13, 16777215);
            }
        }
        if (!duplicates.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, "Duplicate Mods:", this.width / 2, i += 20, 16777215);
            for (Object dep: duplicates) {
                this.drawCenteredString(this.fontRendererObj, String.valueOf(dep), this.width / 2, i += 13, 16777215);
            }
        }
        super.drawScreen(var1, var2, var3);
    }

    @Override
    protected void actionPerformed(GuiButton var1) {
        Minecraft.getMinecraft().shutdown();
    }
}