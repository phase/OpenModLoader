package xyz.openmodloader.registry;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * FuelRegistry keeps track of fuels used for smelting. It currently allows registering
 * new fuels, getting a list of items which can be used as fuels, getting a list of materials
 * which can be used as fuels and blacklist of items which can't be used as fuels.
 *
 * Purpose of this registry is to improve interoperability between mods and between
 * mods and game.
 */
public class FuelRegistry {

    /**
     * Map containing time in game ticks items burn in furnaces.
     */
    private static final Map<Item, Integer> itemBurnTimeMap;

    /**
     * Map containing time in game ticks certain materials burn in furnaces.
     */
    private static final Map<Object, Integer> materialBurnTimeMap;

    /**
     * List containing items that can't be used as fuel.
     */
    private static final List<Item> itemBurnBlacklist;

    static {
        itemBurnTimeMap = new HashMap<>();
        materialBurnTimeMap = new HashMap<>();
        itemBurnBlacklist = new ArrayList<>();

        registerItemBurnTime(Item.getItemFromBlock(Blocks.WOODEN_SLAB), 150);
        registerItemBurnTime(Item.getItemFromBlock(Blocks.COAL_BLOCK), 16000);
        registerItemBurnTime(Item.getItemFromBlock(Blocks.SAPLING), 100);
        registerItemBurnTime(Items.STICK, 100);
        registerItemBurnTime(Items.COAL, 1600);
        registerItemBurnTime(Items.LAVA_BUCKET, 20000);
        registerItemBurnTime(Items.BLAZE_ROD, 2400);
        registerMaterialBurnTime(Material.WOOD, 300);
        registerMaterialBurnTime(Item.a.WOOD, 200);
    }

    /**
     * Registers item and it's burn time into registry. If the item is already registered
     * old burn time will be replaced with new one. Burn time can't be smaller than 0.
     *
     * @param item item to register burn time for.
     * @param burnTime time in game ticks this item will last as fuel.
     * @return true if registration was successful, false if burn time is smaller than 0.
     */
    public static boolean registerItemBurnTime(@Nonnull Item item, int burnTime) {
        if (burnTime < 0) {
            return false;
        }
        itemBurnTimeMap.put(item, burnTime);
        return true;
    }

    /**
     * Stores item and it's burn time into registry. If the item is already registered
     * bigger burn time between already registered one and argument one will be used.
     *
     * @param item item to register burn time for.
     * @param burnTime time in game ticks this item will last as fuel.
     * @return used burn time.
     */
    public static int registerItemBurnTimeHigh(@Nonnull Item item, int burnTime) {
        if (burnTime < 0 && getItemBurnTimeStrict(item) == 0) {
            return 0;
        }
        if (burnTime > getItemBurnTimeStrict(item)) {
            itemBurnTimeMap.put(item, burnTime);
            return burnTime;
        } else {
            return getItemBurnTimeStrict(item);
        }
    }

    /**
     * Registers item and it's burn time into registry. If the item is already registered
     * smaller burn time between already registered one and argument one will be used.
     *
     * @param item item to register burn time for.
     * @param burnTime time in game ticks this item will last as fuel.
     * @return used burn time.
     */
    public static int registerItemBurnTimeLow(@Nonnull Item item, int burnTime) {
        if (burnTime < 0) {
            return getItemBurnTimeStrict(item) > 0 ? getItemBurnTimeStrict(item) : 0;
        }
        if (burnTime < getItemBurnTimeStrict(item)) {
            itemBurnTimeMap.put(item, burnTime);
            return burnTime;
        } else {
            return getItemBurnTimeStrict(item);
        }
    }

    /**
     * Registers burn time for specified block material.
     *
     * @param material block material to register burn time for.
     * @param burnTime time in game ticks this material will last as fuel.
     * @return true if registration was successful, false if burn time is smaller than 0.
     */
    public static boolean registerMaterialBurnTime(@Nonnull Material material, int burnTime) {
        if (burnTime < 0) {
            return false;
        }
        materialBurnTimeMap.put(material, burnTime);
        return true;
    }

    /**
     * Registers burn time for specified item material.
     *
     * @param itemMaterial item material to register burn time for.
     * @param burnTime time in game ticks this material will last as fuel.
     * @return true if registration was successful, false if burn time is smaller than 0.
     */
    public static boolean registerMaterialBurnTime(@Nonnull Item.a itemMaterial, int burnTime) {
        if (burnTime < 0) {
            return false;
        }
        materialBurnTimeMap.put(itemMaterial, burnTime);
        return true;
    }

    /**
     * Adds item to burning blacklist.
     *
     * @param item item to add to blacklist.
     * @return false if item was already on blacklist, true otherwise.
     */
    public boolean blacklistItem(@Nonnull Item item) {
        if (isItemBlacklisted(item)) {
            return false;
        }
        itemBurnBlacklist.add(item);
        return true;
    }

    /**
     * Removes item from burning blacklist.
     *
     * @param item item to remove from blacklist.
     * @return true if item was removed from blacklist.
     */
    public boolean whitelistItem(@Nonnull Item item) {
        if (!isItemBlacklisted(item)) {
            return false;
        }
        itemBurnBlacklist.remove(item);
        return true;
    }

    /**
     * Returns burn time of argument item. This function only returns burn time for
     * registered item and does not check for item material or if item is blacklisted
     * as a fuel.
     *
     * @param item item to check burn time for.
     * @return time argument item can last as a fuel.
     */
    public static int getItemBurnTimeStrict(@Nonnull Item item) {
        if (itemBurnTimeMap.containsKey(item)) {
            return itemBurnTimeMap.get(item);
        } else {
            return 0;
        }
    }

    /**
     * Getter for block material burn time.
     *
     * @param material block material to check burn time for.
     * @return time argument block material can last as a fuel.
     */
    public static int getMaterialBurnTime(@Nonnull Material material) {
        return materialBurnTimeMap.get(material);
    }

    /**
     * Getter for item material burn time.
     *
     * @param material item material to check burn time for.
     * @return time argument material can last as a fuel.
     */
    public static int getMaterialBurnTime(@Nonnull Item.a material) {
        return materialBurnTimeMap.get(material);
    }

    /**
     * Checks if argument item is blacklisted.
     *
     * @param item item to check blacklist for.
     * @return true if item is blacklisted.
     */
    public static boolean isItemBlacklisted(@Nonnull Item item) {
        return itemBurnBlacklist.contains(item);
    }

    /**
     * This is adaptation of {@link #getItemBurnTime(Item)} which accepts item stack.
     * Check {@link #getItemBurnTime(Item)} for detailed method description.
     *
     * @param itemStack block to get burn time of.
     * @return item stack burn time.
     */
    public static int getItemBurnTime(ItemStack itemStack) {
        if (itemStack == null) {
            return 0;
        }
        return getItemBurnTime(itemStack.getItem());
    }

    /**
     * This is adaptation of {@link #getItemBurnTime(Item)} which accepts blocks.
     * Check {@link #getItemBurnTime(Item)} for detailed method description.
     *
     * @param block block to get burn time of.
     * @return block burn time.
     */
    public static int getBlockBurnTime(Block block) {
        if (block == null) {
            return 0;
        }
        return getItemBurnTime(Item.getItemFromBlock(block));
    }

    /**
     * Returns item burn time. This method is used by furnaces to determine item burn time.
     * This method checks if item is blacklisted, if it's not - it checks if item has it's
     * own burn time specified, if it doesn't have it's own burn time specified it checks
     * if material of this item has registered burn time and if it's material isn't
     * registered or this item is blacklisted returns 0.
     *
     * @param item item to get burn time of.
     * @return 0 if item is blacklisted or doesn't have specified burn time; otherwise
     *         (with priority from left to right) returns custom item burn time or material
     *         burn time.
     */
    public static int getItemBurnTime(Item item) {
        if (item == null || isItemBlacklisted(item)) {
            return 0;
        }
        if (itemBurnTimeMap.containsKey(item)) {
            return getItemBurnTimeStrict(item);
        } else {
            if (item instanceof ItemBlock) {
                Block itemOrigin = ((ItemBlock) item).getBlock();
                Material blockMaterial = itemOrigin.getDefaultState().getMaterial();
                if (materialBurnTimeMap.containsKey(blockMaterial)) {
                    return getMaterialBurnTime(blockMaterial);
                }
            } else {
                Item.a itemMaterial;
                if (item instanceof ItemTool) {
                    itemMaterial = ((ItemTool) item).getToolMaterial();
                } else if (item instanceof ItemSword) {
                    itemMaterial = ((ItemSword) item).getMaterial();
                } else if (item instanceof ItemHoe) {
                    itemMaterial = ((ItemHoe) item).getMaterial();
                } else {
                    itemMaterial = null;
                }
                if (itemMaterial != null && materialBurnTimeMap.containsKey(itemMaterial)) {
                    return getMaterialBurnTime(itemMaterial);
                }
            }
        }

        return 0;
    }

    /**
     * Checks if stack item is considered a fuel (has burn time bigger than 0) or not.
     *
     * @param itemStack item stack to check.
     * @return true if stack item is a fuel.
     */
    public static boolean isItemFuel(ItemStack itemStack) {
        return isItemFuel(itemStack.getItem());
    }

    /**
     * Checks if item is considered a fuel (has burn time bigger than 0) or not.
     *
     * @param item item to check.
     * @return true if item is a fuel.
     */
    public static boolean isItemFuel(Item item) {
        return getItemBurnTime(item) > 0;
    }

    /**
     * Used to get read-only access to item burn time registry map.
     *
     * @return item to burn time (in game ticks) map.
     */
    public static Map<Item, Integer> getItemBurnTimeMap() {
        return Collections.unmodifiableMap(itemBurnTimeMap);
    }

    /**
     * Used to get read-only access to material burn time registry map.
     * This map keys are either of type {@link net.minecraft.block.material.Material Material}
     * or value from {@link net.minecraft.item.Item.a Item.Material} enum.
     *
     * @return material to burn time (in game ticks) map.
     */
    public static Map<Object, Integer> getMaterialBurnTimeMap() {
        return Collections.unmodifiableMap(materialBurnTimeMap);
    }

    /**
     * Used to get read-only access to registry list containing blacklisted items.
     *
     * @return list containing items blacklisted from being used as fuels.
     */
    public static List<Item> getItemBurnBlacklist() {
        return Collections.unmodifiableList(itemBurnBlacklist);
    }
}
