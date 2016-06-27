package xyz.openmodloader.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;
import xyz.openmodloader.modloader.ModContainer;
import xyz.openmodloader.modloader.version.UpdateManager;

@Strippable(side = Side.CLIENT)
public class GuiModInfo extends GuiScreen {
    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation ICON_UPDATE = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");

    private GuiModList parent;
    private ModContainer container;
    private ResourceLocation logo;

    public GuiModInfo(GuiModList parent, ModContainer container) {
        this.parent = parent;
        this.container = container;
        this.logo = container.getLogoTexture();
    }

    @Override
    public void initGui() {
        this.func_189646_b(new GuiButton(0, this.width / 2 - 100, this.height - 30, I18n.format("gui.done")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        int width = this.mc.fontRendererObj.getStringWidth(this.container.getName() + " " + this.container.getVersion().toString()) + 94;
        if (UpdateManager.isModOutdated(container)) width = Math.max(width, 94 + this.mc.fontRendererObj.getStringWidth(I18n.format("oml.mod.update", UpdateManager.getUpdateContainer(container).getLatestVersion())));
        int left = this.width / 2 - width / 2;
        int right = left + width;
        int top = 10;
        int bottom = top + 84;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        this.mc.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);

        float scale = 32.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(left, bottom, 0.0D).tex(left / scale, bottom / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
        buffer.pos(right, bottom, 0.0D).tex(right / scale, bottom / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
        buffer.pos(right, top, 0.0D).tex(right / scale, top / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
        buffer.pos(left, top, 0.0D).tex(left / scale, top / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        this.mc.getTextureManager().bindTexture(this.logo != null ? this.logo : ICON_MISSING);
        Gui.drawModalRectWithCustomSizedTexture(left + 10, 20, 0.0F, 0.0F, 64, 64, 64.0F, 64.0F);

        if (UpdateManager.isModOutdated(container)) {
            this.mc.getTextureManager().bindTexture(ICON_UPDATE);
            int v = (System.currentTimeMillis() / 800l & 1l) == 1l ? 8 : 0;
            Gui.drawModalRectWithCustomSizedTexture(left + 10 + 64 - 8, 20 + 64 - 8, 0, v, 8, 8, 8, 16);
        }
        GlStateManager.disableBlend();

        this.mc.fontRendererObj.drawString(this.container.getName(), left + 84, top + 33, 0xFFFFFFFF);
        this.mc.fontRendererObj.drawString(" " + this.container.getVersion().toString(), left + 84 + this.mc.fontRendererObj.getStringWidth(this.container.getName()), top + 33, 8421504);
        this.mc.fontRendererObj.drawString(this.container.getAuthor(), left + 84, top + 43, 0xFFFFFFFF);

        if (UpdateManager.isModOutdated(container)) {
            this.mc.fontRendererObj.drawString(I18n.format("oml.mod.update", UpdateManager.getUpdateContainer(container).getLatestVersion()), left + 84, top + 53, 0xFFFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        }
    }
}
