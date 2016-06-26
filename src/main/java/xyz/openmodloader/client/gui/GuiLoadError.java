package xyz.openmodloader.client.gui;

import java.util.Collection;

import net.minecraft.client.gui.GuiScreen;

public class GuiLoadError extends GuiScreen {

    private final Collection<String> missing;
    private final Collection<String> outdated;
    private final Collection<String> duplicates;

    public GuiLoadError(Collection<String> missing, Collection<String> oudated, Collection<String> duplicates) {
        this.missing = missing;
        this.outdated = oudated;
        this.duplicates = duplicates;
    }

    @Override
    public void drawScreen(int var1, int var2, float var3) {
        this.drawDefaultBackground();
        int i = 0;
        this.drawCenteredString(this.fontRendererObj, "There were several critical errors during load.", this.width / 2, i += 13, 16777215);
        this.drawCenteredString(this.fontRendererObj, "More information may be found in the log.", this.width / 2, i += 13, 16777215);
        if (!missing.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, "Missing Dependencies:", this.width / 2, i += 20, 16777215);
            for (String dep: missing)
                this.drawCenteredString(this.fontRendererObj, dep, this.width / 2, i += 13, 16777215);
        }
        if (!outdated.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, "Outdated Dependencies:", this.width / 2, i += 20, 16777215);
            for (String dep: outdated)
                this.drawCenteredString(this.fontRendererObj, dep, this.width / 2, i += 13, 16777215);
        }
        if (!duplicates.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, "Duplicate Mods:", this.width / 2, i += 20, 16777215);
            for (String dep: duplicates)
                this.drawCenteredString(this.fontRendererObj, dep, this.width / 2, i += 13, 16777215);
        }
        super.drawScreen(var1, var2, var3);
    }
}