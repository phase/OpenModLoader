package xyz.openmodloader.registry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;

public class OMLRegistry {

    private static RegistryNamespacedDefaultedByKey<ResourceLocation, Block> REGISTRY_BLOCK;
    private static ObjectIntIdentityMap<IBlockState> blockIDMap;
    private static RegistryNamespaced<ResourceLocation, Item> REGISTRY_ITEM;
    private static Map<Block, Item> blockItemMap;

    public static RegistryNamespacedDefaultedByKey<ResourceLocation, Block> getBlockRegistry() {
        if (REGISTRY_BLOCK == null) {
            REGISTRY_BLOCK = new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("air"));
        }

        return REGISTRY_BLOCK;
    }

    public static ObjectIntIdentityMap<IBlockState> getBlockIDMap() {
        if (blockIDMap == null) {
            blockIDMap = new ObjectIntIdentityMap<>();
        }
        return blockIDMap;
    }

    public static RegistryNamespaced<ResourceLocation, Item> getItemRegistry() {
        if (REGISTRY_ITEM == null) {
            REGISTRY_ITEM = new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("air"));
        }
        return REGISTRY_ITEM;
    }

    public static Map<Block, Item> getBlockItemMap() {
        if (blockItemMap == null) {
            blockItemMap = new HashMap<>();
        }

        return blockItemMap;
    }
}
