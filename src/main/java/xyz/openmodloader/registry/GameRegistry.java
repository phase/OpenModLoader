package xyz.openmodloader.registry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class GameRegistry {
    public static void registerBlock(Block block) {
//        OMLRegistry.getBlockRegistry().register(600, block.getRegistryName(), block);
//        for (IBlockState state : block.getBlockState().getValidStates())
//            OMLRegistry.getBlockIDMap().put(state, 600 << 4 | block.getMetaFromState(state));
    }

    public static void registerItem(Item item) {
//        OMLRegistry.getItemRegistry().register(700, item.getRegistryName(), item);
    }

    public static void registerItemBlock(ItemBlock item) {
//        OMLRegistry.getItemRegistry().register(Block.getIdFromBlock(item.getBlock()), Block.REGISTRY.getNameForObject(item.getBlock()), item);
//        OMLRegistry.getBlockItemMap().put(item.getBlock(), item);
    }
}
