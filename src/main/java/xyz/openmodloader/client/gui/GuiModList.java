package xyz.openmodloader.client.gui;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;

@Strippable(side = Side.CLIENT)
public class GuiModList extends GuiScreen {
    private GuiScreen parent;
    private GuiButton infoButton;
    private GuiTextField searchField;
    private GuiModListSelection selectionList;

    public GuiModList(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.func_189646_b(new GuiButton(0, this.width / 2 + 60, this.height - 30, 95, 20, I18n.format("gui.done")));
        this.infoButton = this.func_189646_b(new GuiButton(1, this.width / 2 - 47, this.height - 30, 95, 20, "Mod Info"));
        this.searchField = new GuiTextField(0, this.mc.fontRendererObj, this.width / 2 - 154, this.height - 29, 95, 18);
        this.selectionList = new GuiModListSelection(this, this.mc, this.width, this.height, 32, this.height - 40, 36);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        this.selectionList.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            GuiModListSelectionEntry entry = this.selectionList.getSelectedEntry();
            if (button.id == 0) {
                this.mc.displayGuiScreen(this.parent);
            } else if (button.id == 1 && entry != null) {
                entry.openModInfo();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.selectionList.drawScreen(mouseX, mouseY, partialTicks);
        this.searchField.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, "Mod List", this.width / 2, 13, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int partialTicks) {
        super.mouseClicked(mouseX, mouseY, partialTicks);
        this.selectionList.mouseClicked(mouseX, mouseY, partialTicks);
        this.searchField.mouseClicked(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int partialTicks) {
        super.mouseReleased(mouseX, mouseY, partialTicks);
        this.selectionList.mouseReleased(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (this.searchField.textboxKeyTyped(character, key)) {
            this.selectionList.refreshList(this.searchField.getText());
        } else {
            super.keyTyped(character, key);
        }
    }

    @Override
    public void updateScreen() {
        this.searchField.updateCursorCounter();
    }

    public void selectEntry(@Nullable GuiModListSelectionEntry entry) {
        this.infoButton.enabled = entry != null;
    }
}
