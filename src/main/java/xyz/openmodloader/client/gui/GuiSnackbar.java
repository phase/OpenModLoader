package xyz.openmodloader.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.event.strippable.Side;
import xyz.openmodloader.event.strippable.Strippable;

import java.util.ArrayList;
import java.util.List;

@Strippable(side = Side.CLIENT)
public class GuiSnackbar extends Gui {
    public static GuiSnackbar CURRENT_SNACKBAR = null;
    public static final List<GuiSnackbar> SNACKBAR_LIST = new ArrayList<>();

    private Minecraft mc;
    private ITextComponent component;
    private int maxAge;
    private int age;
    private float yOffset = 20;

    public GuiSnackbar(ITextComponent component) {
        this.mc = Minecraft.getMinecraft();
        this.component = component;
        this.maxAge = (int) (this.mc.fontRendererObj.getStringWidth(this.component.getUnformattedText()) * 1.5F);
    }

    public void updateSnackbar() {
        this.age++;
        if (this.age > this.maxAge) {
            GuiSnackbar.CURRENT_SNACKBAR = null;
        }
    }

    public void drawSnackbar() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 500.0F);
        GlStateManager.translate(0.0F, -this.yOffset, 0.0F);
        GlStateManager.enableBlend();
        ScaledResolution resolution = new ScaledResolution(this.mc);
        Gui.drawRect(0, 20, resolution.getScaledWidth(), 0, 0xB0000000);
        this.mc.fontRendererObj.drawString(this.component.getFormattedText(), 10, 6, 0xFFFFFFFF);
        GlStateManager.popMatrix();
        this.yOffset = this.interpolate(this.yOffset, this.age < this.maxAge - 10 ? 0.0F : 20.0F, 0.15F);
    }

    private float interpolate(float prev, float current, float factor) {
        return prev + factor * (current - prev);
    }
}
