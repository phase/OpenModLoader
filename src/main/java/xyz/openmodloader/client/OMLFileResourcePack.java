package xyz.openmodloader.client;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.FileResourcePack;
import xyz.openmodloader.modloader.ModContainer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OMLFileResourcePack extends FileResourcePack {
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
		return TextureUtil.readBufferedImage(OMLFolderResourcePack.class.getResourceAsStream("/" + mod.getLogo()));
	}
}
