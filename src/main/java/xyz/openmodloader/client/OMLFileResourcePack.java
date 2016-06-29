package xyz.openmodloader.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.modloader.ModContainer;

public class OMLFileResourcePack extends FileResourcePack {

    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");

    private ModContainer mod;

    public OMLFileResourcePack(ModContainer mod) {
        super(mod.getModFile());
        this.mod = mod;
    }

    @Override
    protected InputStream getInputStreamByName(String name) throws IOException {
        try {
            return super.getInputStreamByName(name);
        } catch (IOException e) {
            if ("pack.mcmeta".equals(name)) {
                return new ByteArrayInputStream(
                            ("{\n" +
                            "\"pack\": {\n" +
                            "\"description\": \"OML dummy resource pack for " + mod.getModID() + "\",\n" +
                            "\"pack_format\": 1\n" +
                            "}\n" +
                            "}").getBytes());
            } else {
                throw e;
            }
        }
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        InputStream in = null;
        if (mod.getLogoTexture() == null) {
            in = Minecraft.getMinecraft().getResourceManager().getResource(ICON_MISSING).getInputStream();
        } else {
            in = Minecraft.getMinecraft().getResourceManager().getResource(mod.getLogoTexture()).getInputStream();
        }
        return TextureUtil.readBufferedImage(in);
    }
}
