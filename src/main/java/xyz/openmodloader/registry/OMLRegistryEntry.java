package xyz.openmodloader.registry;

import net.minecraft.util.ResourceLocation;

public class OMLRegistryEntry<T> {
    private ResourceLocation name;

    public T setRegistryName(ResourceLocation name) {
        this.name = name;
        return (T) this;
    }

    public ResourceLocation getRegistryName() {
        return name;
    }
}
