package xyz.openmodloader.modloader;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import xyz.openmodloader.event.strippable.Side;
import xyz.openmodloader.event.strippable.Strippable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ModContainer {
    private transient Class<?> mainClass;
    private transient ResourceLocation logo;
    private transient IMod instance;

    @SerializedName("Mod-Class")
    private String classString;
    @SerializedName("ID")
    private String modID;
    @SerializedName("Name")
    private String name;
    @SerializedName("Major")
    private String major;
    @SerializedName("Minor")
    private String minor;
    @SerializedName("Patch")
    private String patch;
    @SerializedName("Description")
    private String description;
    @SerializedName("UpdateURL")
    private String updateURL;
    @SerializedName("Logo")
    private String logoString;

    public static ModContainer create(Manifest manifest) {
        Set<Object> attributeNames = manifest.getMainAttributes().keySet();
        if (!attributeNames.containsAll(Arrays.asList(new Attributes.Name("Mod-Class"), new Attributes.Name("ID")))) {
            return null;
        }
        ModContainer container = new ModContainer();
        Map<Object, Object> attributes = manifest.getMainAttributes();
        for (Field field : ModContainer.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(SerializedName.class)) {
                Attributes.Name name = new Attributes.Name(field.getAnnotation(SerializedName.class).value());
                if (attributes.containsKey(name)) {
                    try {
                        field.set(container, attributes.get(name));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return container;
    }

    public Class<?> getMainClass() {
        if (this.mainClass == null) {
            try {
                this.mainClass = Class.forName(this.classString);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return mainClass;
    }

    @Strippable(side = Side.CLIENT)
    public ResourceLocation getLogo() {
        if (this.logo == null) {
            try {
                BufferedImage image = ImageIO.read(ModContainer.class.getResourceAsStream("/" + this.logoString));
                DynamicTexture texture = new DynamicTexture(image);
                this.logo = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("mods/" + getModID(), texture);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return logo;
    }

    public IMod getInstance() {
        if (this.instance == null) {
            try {
                this.instance = (IMod) this.getMainClass().newInstance();
            } catch (ClassCastException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public String getModID() {
        return modID;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getPatch() {
        return patch;
    }

    public String getVersion() {
        return getMajor() + "." + getMinor() + "." + getPatch();
    }

    public String getDescription() {
        return description;
    }

    public String getUpdateURL() {
        return updateURL;
    }
}
