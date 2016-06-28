package xyz.openmodloader.dictionary;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Objects;

/**
 * A wrapper around ItemStack that contains
 * versions of {@link #hashCode()} and {@link #equals(Object)}
 * that take the item, the item damage, and the NBT into
 * account, but not the stack size.
 */
public class ItemStackWrapper {

    private final ItemStack stack;

    /**
     * Instantiates a new item stack wrapper.
     *
     * @param block the block - can be null
     */
    public ItemStackWrapper(Block block) {
        this(Item.getItemFromBlock(block));
    }

    /**
     * Instantiates a new item stack wrapper.
     *
     * @param item the item - can be null
     */
    public ItemStackWrapper(Item item) {
        this(item != null ? new ItemStack(item) : null);
    }

    /**
     * Instantiates a new item stack wrapper.
     *
     * @param stack the stack - can be null
     */
    public ItemStackWrapper(ItemStack stack) {
        this.stack = stack;
    }

    /**
     * Instantiates a new item stack wrapper with a null stack.
     */
    public ItemStackWrapper() {
        this.stack = null;
    }

    @Override
    public int hashCode() {
        return stack != null ? Objects.hash(stack.getItem(), stack.getMetadata(), stack.getTagCompound()) : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ItemStackWrapper)) return false;

        ItemStackWrapper wrapper = (ItemStackWrapper) obj;
        return ItemStack.areItemStacksEqual(stack, wrapper.stack) && ItemStack.areItemStackTagsEqual(stack, wrapper.stack);
    }

    @Override
    public String toString() {
        return stack != null ? stack.getItem().getUnlocalizedName() + "@" + stack.getMetadata() : "null";
    }
}