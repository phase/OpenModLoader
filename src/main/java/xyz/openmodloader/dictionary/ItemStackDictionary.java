package xyz.openmodloader.dictionary;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * An extended version of Dictionary for ItemStacks,
 * with a few utility methods.
 */
public class ItemStackDictionary extends Dictionary<ItemStackWrapper> {

    /**
     * Registers an item stack.
     *
     * @param key the key
     * @param value the value
     */
    public void register(String key, ItemStack value) {
        register(key, new ItemStackWrapper(value));
    }

    /**
     * Registers an item.
     *
     * @param key the key
     * @param value the value
     */
    public void register(String key, Item value) {
        register(key, new ItemStackWrapper(new ItemStack(value)));
    }

    /**
     * Registers a block.
     *
     * @param key the key
     * @param value the value
     */
    public void register(String key, Block value) {
        register(key, new ItemStackWrapper(new ItemStack(value)));
    }
}