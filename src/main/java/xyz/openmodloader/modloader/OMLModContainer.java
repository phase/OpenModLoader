package xyz.openmodloader.modloader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;

class OMLModContainer implements ModContainer {

    private ResourceLocation logo;

    @Override
    public Version getVersion() {
        return OpenModLoader.INSTANCE.getVersion();
    }

    @Override
    public String getModID() {
        return "oml";
    }

    @Override
    public String getName() {
        return "OpenModLoader";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getAuthor() {
        return "OML";
    }

    @Override
    @Strippable(side = Side.CLIENT)
    public ResourceLocation getLogo() {
        if (this.logo == null) {
            try {
                InputStream stream = this.getClass().getResourceAsStream("/logo.png");
                BufferedImage image = ImageIO.read(stream);
                DynamicTexture texture = new DynamicTexture(image);
                this.logo = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("mods/" + getModID(), texture);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return logo;
    }

    @Override
    public IMod getInstance() {
        return null;
    }

    @Override
    public String getURL() {
        return "https://openmodloader.xyz";
    }

    @Override
    public String getUpdateURL() {
        return null;
    }

    @Override
    public String[] getTransformers() {
        return new String[0];
    }

    @Override
    public String[] getDependencies() {
        return new String[0];
    }
}
