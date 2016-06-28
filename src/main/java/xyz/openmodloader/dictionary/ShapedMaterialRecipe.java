package xyz.openmodloader.dictionary;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.*;

/**
 * Shaped recipe that uses the material dictionary
 */
public class ShapedMaterialRecipe implements IRecipe {
    private final List<Object> items;
    private final ItemStack output;
    private final int size;
    private int width;
    private int height;

    /**
     * Creates a new shaped material recipe
     *
     * @param width  recipe width
     * @param height recipe height
     * @param items  recipe input (item stack wrappers and/or sets of stack wrappers)
     * @param output recipe output
     */
    public ShapedMaterialRecipe(int width, int height, List<Object> items, ItemStack output) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        this.items = items;
        this.output = output;
    }

    /**
     * Creates a new shaped material recipe
     *
     * @param output recipe output
     * @param input  recipe input
     * @see net.minecraft.item.crafting.CraftingManager
     */
    public ShapedMaterialRecipe(ItemStack output, Object... input) {
        this.output = output;

        int index = 0;
        width = 0;
        height = 0;
        StringBuilder sb = new StringBuilder();

        if (input[index] instanceof String[]) {
            for (String s : (String[]) input[index++]) {
                ++height;
                width = s.length();
                sb.append(s);
            }
        } else {
            while (input[index] instanceof String) {
                String s = (String) input[index++];
                ++height;
                width = s.length();
                sb.append(s);
            }
        }

        String shape = sb.toString();

        Map<Character, Object> map = new HashMap<>();

        while (index < input.length) {
            Character character = (Character) input[index];
            Object obj = input[index + 1];

            if (obj instanceof Item) {
                map.put(character, new ItemStackWrapper((Item) obj));
            } else if (obj instanceof Block) {
                map.put(character, new ItemStackWrapper((Block) obj));
            } else if (obj instanceof ItemStack) {
                map.put(character, new ItemStackWrapper((ItemStack) obj));
            } else if (obj instanceof String) {
                map.put(character, Dictionaries.MATERIALS.get((String) obj));
            }

            index += 2;
        }

        size = width * height;
        items = Arrays.asList(new Set[size]);

        for (int slot = 0; slot < size; ++slot) {
            char character = shape.charAt(slot);
            Object obj = map.get(character);

            if (obj != null) {
                items.set(slot, obj);
            } else {
                items.set(slot, new ItemStackWrapper());
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
        for (int x = 0; x <= 3 - width; ++x) {
            for (int y = 0; y <= 3 - height; ++y) {
                if (checkMatch(inv, x, y, true)) {
                    return true;
                }

                if (checkMatch(inv, x, y, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean isMirror) {
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                int subX = x - startX;
                int subY = y - startY;
                Object obj;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                    if (isMirror) {
                        obj = items.get(width - subX - 1 + subY * width);
                    } else {
                        obj = items.get(subX + subY * width);
                    }
                } else {
                    obj = new ItemStackWrapper();
                }

                ItemStackWrapper wrapper = new ItemStackWrapper(inv.getStackInRowAndColumn(x, y));

                if (obj instanceof Set) {
                    if (!((Set) obj).contains(wrapper)) {
                        return false;
                    }
                } else {
                    if (!obj.equals(wrapper)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return output.copy();
    }

    @Override
    public int getRecipeSize() {
        return size;
    }
}
