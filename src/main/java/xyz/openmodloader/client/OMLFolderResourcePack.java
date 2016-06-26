package xyz.openmodloader.client;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.FolderResourcePack;
import xyz.openmodloader.modloader.ModContainer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class OMLFolderResourcePack extends FolderResourcePack {
	private ModContainer mod;

	public OMLFolderResourcePack(ModContainer mod) {
		super(mod.getModFile());
		this.mod = mod;
	}

	@Override
	public BufferedImage getPackImage() throws IOException {
		return TextureUtil.readBufferedImage(OMLFolderResourcePack.class.getResourceAsStream("/" + mod.getLogo()));
	}
}
