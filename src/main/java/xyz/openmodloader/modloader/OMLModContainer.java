package xyz.openmodloader.modloader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;
import xyz.openmodloader.modloader.version.Version;

class OMLModContainer implements ModContainer {

    private File modFile;
    private ResourceLocation logo;

    public OMLModContainer() {
        modFile = new File(OMLModContainer.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    @Override
    public Version getVersion() {
        return OpenModLoader.INSTANCE.getVersion();
    }

    @Override
    public Version getMinecraftVersion() {
        return OpenModLoader.INSTANCE.getMinecraftVersion();
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
    public String getLogo() {
        return "logo.png";
    }

    @Override
    @Strippable(side = Side.CLIENT)
    public ResourceLocation getLogoTexture() {
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
    public Mod getInstance() {
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

    @Override
    public Side getSide() {
        return Side.UNIVERSAL;
    }

    @Override
    public File getModFile() {
        return modFile;
    }
}
