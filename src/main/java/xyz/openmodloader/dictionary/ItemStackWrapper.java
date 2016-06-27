package xyz.openmodloader.dictionary;

import net.minecraft.item.ItemStack;

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
     * @param stack the stack - can be null
     */
    public ItemStackWrapper(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int hashCode() {
        if (stack == null) {
            return 0;
        }
        final int prime = 31;
        int result = 1;
        result = prime * result + ((stack.getItem() == null) ? 0 : stack.getItem().hashCode());
        result = prime * result + stack.getItemDamage();
        result = prime * result + ((stack.getTagCompound() == null) ? 0 : stack.getTagCompound().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemStack other = ((ItemStackWrapper) obj).stack;
        return ItemStack.areItemsEqual(stack, other) && ItemStack.areItemStackTagsEqual(stack, other);
    }
}