package xyz.openmodloader.dictionary;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class MaterialRecipeShaped implements IRecipe {

    private final List<Set<ItemStackWrapper>> input;
    private final ItemStack output;

    public MaterialRecipeShaped(ItemStack output, Object... input) {
        this.input = Lists.newArrayListWithExpectedSize(9);
        for (Object obj: input) {
            if (obj instanceof ItemStack) {
                this.input.add(Collections.singleton(new ItemStackWrapper((ItemStack) obj)));
            } else if (obj instanceof Item) {
                this.input.add(Collections.singleton(new ItemStackWrapper(new ItemStack((Item) obj))));
            } else if (obj instanceof Block) {
                this.input.add(Collections.singleton(new ItemStackWrapper(new ItemStack((Block) obj))));
            } else if (obj instanceof String) {
                this.input.add(Dictionary.MATERIALS.get((String) obj));
            } else if (obj == null) {
                this.input.add(Collections.singleton(new ItemStackWrapper(null)));
            }
        }
        while (this.input.size() < 9) {
            this.input.add(Collections.singleton(new ItemStackWrapper(null)));
        }
        this.output = output;
    }

    @Override
    public boolean matches(InventoryCrafting var1, World var2) {
        for (int i = 0; i < var1.getSizeInventory(); i++) {
            ItemStack stack = var1.getStackInSlot(i);
            Set<ItemStackWrapper> input = this.input.get(i);
            if (!input.contains(new ItemStackWrapper(stack))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        return output.copy();
    }

    @Override
    public int getRecipeSize() {
        return 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output.copy();
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] stacks = new ItemStack[inv.getSizeInventory()];
        for (int i = 0; i < stacks.length; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if(stack != null && stack.getItem().hasContainerItem()) {
                stacks[i] = new ItemStack(stack.getItem().getContainerItem());
            }
        }
        return stacks;
    }
}
