package xyz.openmodloader.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

public class GuiMissingDependencies extends GuiScreen {

    private final List<String> missing;
    private final List<String> outdated;

    public GuiMissingDependencies(List<String> missing, List<String> oudated) {
        this.missing = missing;
        this.outdated = oudated;
    }

    @Override
    public void drawScreen(int var1, int var2, float var3) {
        this.drawDefaultBackground();
        int i = 0;
        this.drawCenteredString(this.fontRendererObj, "Missing Dependencies:", this.width / 2, i += 13, 16777215);
        for (String dep: missing)
            this.drawCenteredString(this.fontRendererObj, dep, this.width / 2, i += 13, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Outdated Dependencies:", this.width / 2, i += 13, 16777215);
        for (String dep: outdated)
            this.drawCenteredString(this.fontRendererObj, dep, this.width / 2, i += 13, 16777215);
        super.drawScreen(var1, var2, var3);
    }
}