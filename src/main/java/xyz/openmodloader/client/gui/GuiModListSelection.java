package xyz.openmodloader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import xyz.openmodloader.modloader.ModLoader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GuiModListSelection extends GuiListExtended {
    private GuiModList parent;
    private List<GuiModListSelectionEntry> entries = new ArrayList<>();
    private int selectedIndex = -1;

    public GuiModListSelection(GuiModList parent, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
        super(mc, width, height, top, bottom, slotHeight);
        this.parent = parent;
        this.refreshList("");
    }

    public void refreshList(String search) {
        this.selectEntry(-1);
        this.entries.clear();
        this.entries.addAll(ModLoader.MODS.stream().filter(mod -> mod.getName().toLowerCase(Locale.ENGLISH).contains(search.toLowerCase(Locale.ENGLISH)) || mod.getModID().toLowerCase(Locale.ENGLISH).contains(search.toLowerCase(Locale.ENGLISH))).map(mod -> new GuiModListSelectionEntry(this, mod)).collect(Collectors.toList()));
    }

    @Override
    public GuiModListSelectionEntry getListEntry(int index) {
        return this.entries.get(index);
    }

    @Override
    protected int getSize() {
        return this.entries.size();
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 20;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 50;
    }

    public void selectEntry(int index) {
        this.selectedIndex = index;
        this.parent.selectEntry(this.getSelectedEntry());
    }

    @Override
    protected boolean isSelected(int index) {
        return index == this.selectedIndex;
    }

    @Nullable
    public GuiModListSelectionEntry getSelectedEntry() {
        return this.selectedIndex >= 0 && this.selectedIndex < this.getSize() ? this.getListEntry(this.selectedIndex) : null;
    }

    public GuiModList getParent() {
        return this.parent;
    }
}
