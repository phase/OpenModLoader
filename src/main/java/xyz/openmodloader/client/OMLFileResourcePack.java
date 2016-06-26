package xyz.openmodloader.client;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.FileResourcePack;
import xyz.openmodloader.modloader.ModContainer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class OMLFileResourcePack extends FileResourcePack {
	private ModContainer mod;

	public OMLFileResourcePack(ModContainer mod) {
		super(mod.getModFile());
		this.mod = mod;
	}

	@Override
	public BufferedImage getPackImage() throws IOException {
		return TextureUtil.readBufferedImage(OMLFolderResourcePack.class.getResourceAsStream("/" + mod.getLogo()));
	}
}
