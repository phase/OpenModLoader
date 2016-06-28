package xyz.openmodloader.dictionary;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Shapeless recipe that uses the material dictionary
 */
public class ShapelessMaterialRecipe implements IRecipe {
    private final ItemStack output;
    private final List<Object> items;

    /**
     * Creates a new shapeless material recipe
     *
     * @param items  recipe input (item stack wrappers and/or sets of stack wrappers)
     * @param output recipe output
     */
    public ShapelessMaterialRecipe(ItemStack output, List<Object> items) {
        this.output = output;
        this.items = items;
    }

    /**
     * Creates a new shapeless material recipe
     *
     * @param output recipe output
     * @param input  recipe input
     * @see net.minecraft.item.crafting.CraftingManager
     */
    public ShapelessMaterialRecipe(ItemStack output, Object... input) {
        this.output = output;
        this.items = new ArrayList<>();

        for (Object obj : input) {
            if (obj instanceof ItemStack) {
                items.add(new ItemStackWrapper(((ItemStack) obj).copy()));
            } else if (obj instanceof Item) {
                items.add(new ItemStackWrapper((Item) obj));
            } else if (obj instanceof Block) {
                items.add(new ItemStackWrapper((Block) obj));
            } else if (obj instanceof String) {
                items.add(Dictionaries.MATERIALS.get((String) obj));
            }
        }
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] stacks = new ItemStack[inv.getSizeInventory()];

        for (int slot = 0; slot < stacks.length; ++slot) {
            ItemStack stack = inv.getStackInSlot(slot);

            if (stack != null && stack.getItem().hasContainerItem()) {
                stacks[slot] = new ItemStack(stack.getItem().getContainerItem());
            }
        }

        return stacks;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        List<Object> remaining = new ArrayList<>(items);

        matching:
        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);

            if (stack == null) {
                continue;
            }

            ItemStackWrapper wrapper = new ItemStackWrapper(stack);

            for (Iterator<Object> iterator = remaining.iterator(); iterator.hasNext(); ) {
                Object object = iterator.next();

                if (object instanceof Set) {
                    if (((Set) object).contains(wrapper)) {
                        iterator.remove();
                        continue matching;
                    }
                } else {
                    if (object.equals(wrapper)) {
                        iterator.remove();
                        continue matching;
                    }
                }
            }

            return false;
        }

        return remaining.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        return output.copy();
    }

    @Override
    public int getRecipeSize() {
        return items.size();
    }
}
