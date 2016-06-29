package xyz.openmodloader.modloader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.modloader.version.Version;

class ManifestModContainer implements ModContainer {

    private transient Class<?> mainClass;
    private transient ResourceLocation logoTexture;
    private transient Mod instance;
    private transient Version version;
    private transient Version mcversion;
    private transient Side side;
    private transient File modFile;
    private transient byte[] logoBytes;

    @SerializedName("Mod-Class")
    private String classString;
    @SerializedName("ID")
    private String modID;
    @SerializedName("Name")
    private String name;
    @SerializedName("Version")
    private String versionString;
    @SerializedName("Minecraft-Version")
    private String mcversionString;
    @SerializedName("Side")
    private String sideString;
    @SerializedName("Description")
    private String description;
    @SerializedName("Author")
    private String author;
    @SerializedName("URL")
    private String url;
    @SerializedName("Update-URL")
    private String updateURL;
    @SerializedName("Logo")
    private String logo;
    @SerializedName("Transformers")
    private String transformers;
    @SerializedName("Dependencies")
    private String dependencies;

    /**
     * Uses a manifest to create a mod container.
     */
    public static ManifestModContainer create(File modFile, Manifest manifest) {
        Set<Object> attributeNames = manifest.getMainAttributes().keySet();
        if (!attributeNames.containsAll(Arrays.asList(new Attributes.Name("ID"), new Attributes.Name("Version")))) {
            return null;
        }
        ManifestModContainer container = new ManifestModContainer();
        Attributes attributes = manifest.getMainAttributes();
        for (Field field : ManifestModContainer.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(SerializedName.class)) {
                String name = field.getAnnotation(SerializedName.class).value();
                if (attributeNames.contains(new Attributes.Name(name))) {
                    try {
                        field.set(container, attributes.getValue(name));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        container.modFile = modFile;
        return container;
    }

    public Class<?> getMainClass() {
        if (classString == null) {
            return null;
        }
        if (this.mainClass == null) {
            try {
                this.mainClass = Class.forName(this.classString, true, Launch.classLoader);
                if (!Mod.class.isAssignableFrom(mainClass)) {
                    throw new RuntimeException("Mod class '" + mainClass.getName() + "' for mod '" + name + " does not implement " + Mod.class.getName());
                }
            } catch (ClassNotFoundException e) {
                if (e.getCause() instanceof RuntimeException && e.getCause().getMessage().equals("Loading class " + classString + " on wrong side " + OpenModLoader.getSidedHandler().getSide())) {
                    throw new RuntimeException("Mod '" + getName() + " is using @Strippable(side = X) on its main class. Use the Side field in the manifest instead!", e);
                }
                throw new RuntimeException("Could not find mod class '" + classString + "' for mod '" + name + '\'', e);
            }
        }
        return mainClass;
    }

    @Override
    public ResourceLocation getLogoTexture() {
        if (logoTexture == null && (logoBytes != null || logo != null)) {
            try {
                InputStream in = null;
                if (logoBytes == null) {
                    in = new URL(getModFile().toURI().toURL().toString() + '/' + logo).openStream();;
                } else {
                    in = new ByteArrayInputStream(logoBytes);
                }
                BufferedImage image = TextureUtil.readBufferedImage(in);
                DynamicTexture texture = new DynamicTexture(image);
                this.logoTexture = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("mods/" + getModID(), texture);
                in.close();
                logoBytes = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return logoTexture;
    }

    @Override
    public Mod getInstance() {
        if (this.instance == null) {
            try {
                Class<?> clazz = this.getMainClass();
                if (clazz == null)
                    return null;
                this.instance = (Mod) clazz.newInstance();
            } catch (ClassCastException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public Version getVersion() {
        if (version == null) {
            this.version = new Version(versionString);
        }
        return version;
    }

    @Override
    public Version getMinecraftVersion() {
        if (mcversion == null)
            if (mcversionString == null)
                mcversion = OpenModLoader.getMinecraftVersion();
            else
                mcversion = new Version(mcversionString);
        return mcversion;
    }

    @Override
    public String getModID() {
        return modID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Side getSide() {
        if (side == null)
            if (sideString == null)
                side = Side.UNIVERSAL;
            else
                side = Side.valueOf(sideString);
        return side;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public File getModFile() {
        return modFile;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String getUpdateURL() {
        return updateURL;
    }

    @Override
    public String[] getTransformers() {
        return transformers == null ? ArrayUtils.EMPTY_STRING_ARRAY : transformers.split("\\s*,\\s*");
    }

    @Override
    public String[] getDependencies() {
        if (dependencies == null || dependencies.matches("\\s*")) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return dependencies.split("\\s*,\\s*");
    }

    /**
     * INTERNAL
     */

    Set<String> getDependencySet() {
        Set<String> set = Sets.newHashSet();
        for (String s : getDependencies()) {
            set.add(s.split("\\s:\\s")[0]);
        }
        return set;
    }

    void setLogo(byte[] bs) {
        this.logoBytes = bs;
    }

    String getLogo() {
        return logo;
    }
}
