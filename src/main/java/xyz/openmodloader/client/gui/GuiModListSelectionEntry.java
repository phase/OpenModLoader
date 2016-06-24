package xyz.openmodloader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import xyz.openmodloader.modloader.ModContainer;

public class GuiModListSelectionEntry implements GuiListExtended.a {
    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");

    private GuiModListSelection parent;
    private ModContainer container;
    private Minecraft mc;
    private ResourceLocation logo;
    private long lastClickTime;

    public GuiModListSelectionEntry(GuiModListSelection parent, ModContainer container) {
        this.parent = parent;
        this.container = container;
        this.mc = Minecraft.getMinecraft();
        //this.logo = container.getLogo();
        this.logo = null;
    }

    @Override
    public void drawEntry(int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean mouseHovered) {
        this.mc.fontRendererObj.drawString(this.container.getName(), x + 32 + 3, y + 1, 16777215);
        this.mc.fontRendererObj.drawString(this.container.getModID(), x + 32 + 3, y + this.mc.fontRendererObj.FONT_HEIGHT + 3, 8421504);
        this.mc.fontRendererObj.drawString(this.container.getVersion(), x + 32 + 3, y + this.mc.fontRendererObj.FONT_HEIGHT * 2 + 4, 8421504);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.logo != null ? this.logo : ICON_MISSING);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
    }

    @Override
    public boolean mousePressed(int index, int mouseX, int mouseY, int button, int x, int y) {
        this.parent.selectEntry(index);
        if (Minecraft.getSystemTime() - this.lastClickTime < 250L) {
            this.openModInfo();
            return true;
        } else {
            this.lastClickTime = Minecraft.getSystemTime();
            return false;
        }
    }

    public void openModInfo() {
        this.mc.displayGuiScreen(new GuiModInfo(this.parent.getParent(), this.container));
    }

    @Override
    public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {

    }

    @Override
    public void setSelected(int index, int x, int y) {

    }
}
