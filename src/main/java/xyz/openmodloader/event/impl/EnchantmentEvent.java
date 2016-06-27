package xyz.openmodloader.event.impl;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

import java.util.List;

/**
 * Parent class for enchantment related events. All events that fall within this
 * scope should extend this class. They should also be added as an inner class
 * however this is not required.
 */
public class EnchantmentEvent extends Event {
    /**
     * Fired when the player enchants an item. Allows for the cost and
     * enchantments to be altered, along with the enchanted item itself and the
     * fuel item.
     */
    public static class Item extends EnchantmentEvent {

        /**
         * The player who is enchanting the item.
         */
        private final EntityPlayer player;

        /**
         * The item being enchanted.
         */
        private final ItemStack stack;

        /**
         * The item being used as fuel in the Lapis Lazuli slot.
         */
        private final ItemStack fuel;

        /**
         * The list containing all of the enchantments to be added to the
         * enchanted item.
         */
        private final List<EnchantmentData> enchantments;

        /**
         * The amount of experience levels being paid for the enchantments.
         */
        private int levels;

        /**
         * Constructs an event that is fired when the player enchants an item.
         *
         * @param player The player enchanting the item.
         * @param stack The ItemStack being enchanted.
         * @param fuel The ItemStack in the fuel/lapis slot.
         * @param levels The experience level cost for the enchantment.
         * @param enchantments A list of enchantment data being applied to the
         *        item.
         */
        public Item(EntityPlayer player, ItemStack stack, ItemStack fuel, int levels, List<EnchantmentData> enchantments) {
            this.player = player;
            this.stack = stack;
            this.fuel = fuel;
            this.levels = levels;
            this.enchantments = enchantments;
        }

        /**
         * Gets the player who enchanted the item.
         *
         * @return The player who enchanted the item.
         */
        public EntityPlayer getPlayer() {
            return player;
        }

        /**
         * Gets the ItemStack being enchanted.
         *
         * @return The ItemStack being enchanted.
         */
        public ItemStack getItemStack() {
            return stack;
        }

        /**
         * Gets the ItemStack in the fuel slot. This is where Lapis Lazuli goes.
         *
         * @return The ItemStack in the fuel slot.
         */
        public ItemStack getFuelStack() {
            return fuel;
        }

        /**
         * Gets the list of enchantment data being added to the ItemStack.
         *
         * @return The enchantment data being added to the ItemStack.
         */
        public List<EnchantmentData> getEnchantments() {
            return enchantments;
        }

        /**
         * Gets the experience level cost for the enchantments.
         *
         * @return The experience level cost for the enchantments.
         */
        public int getLevels() {
            return levels;
        }

        /**
         * Sets the experience level cost for the enchantments.
         *
         * @param levels The new experience level cost for the enchantments.
         */
        public void setLevels(int levels) {
            this.levels = levels;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }

        /**
         * Hook to make related patches much cleaner.
         *
         * @param player The player who enchanted the item.
         * @param stack The item being enchanted.
         * @param fuel The item in the fuel slot.
         * @param levels The amount of levels being paid for the enchantment.
         * @param enchantments The list of enchantment data being applied to the
         *        enchanted item.
         * @return The list of enchantments to apply. If this is null or empty,
         *         no enchantments should be applied.
         */
        public static List<EnchantmentData> handle(EntityPlayer player, ItemStack stack, ItemStack fuel, int levels, List<EnchantmentData> enchantments) {
            final Item event = new Item(player, stack, fuel, levels, enchantments);
            return OpenModLoader.getEventBus().post(event) ? event.getEnchantments() : null;
        }
    }

    /**
     * Fired when an enchantment level is being looked up for an entity.
     */
    public static class Level extends EnchantmentEvent {

        /**
         * The entity for which the enchantment is being checked.
         */
        private final EntityLivingBase entityLiving;

        /**
         * The enchantment for which the level is being evaluated.
         */
        private final Enchantment enchantment;

        /**
         * The item the entity is currently holding.
         */
        private final ItemStack heldItem;

        /**
         * The current level of the enchantment.
         */
        private int level;

        /**
         * Constructs an event that is fired when an enchantment level is being
         * looked up.
         *
         * @param entityLiving The entity for which the enchantment is being
         *        checked.
         * @param enchantment The enchantment for which the level is being
         *        evaluated.
         * @param heldItem The item the entity is currently holding.
         * @param level The current level of the enchantment.
         */
        public Level(EntityLivingBase entityLiving, Enchantment enchantment, ItemStack heldItem, int level) {
            this.entityLiving = entityLiving;
            this.enchantment = enchantment;
            this.heldItem = heldItem;
            this.level = level;
        }

        /**
         * Gets the entity for which the enchantment is being checked.
         *
         * @return The entity for which the enchantment is being checked.
         */
        public EntityLivingBase getEntityLiving() {
            return entityLiving;
        }

        /**
         * Gets the enchantment for which the level is being evaluated.
         *
         * @return The enchantment for which the level is being evaluated.
         */
        public Enchantment getEnchantment() {
            return enchantment;
        }

        /**
         * Gets the item the entity is currently holding.
         *
         * @return The item the entity is currently holding.
         */
        public ItemStack getHeldItem() {
            return heldItem;
        }

        /**
         * Gets the current level of the enchantment.
         *
         * @return The current level of the enchantment.
         */
        public int getLevel() {
            return level;
        }

        /**
         * Sets the new level of the enchantment.
         *
         * @param level The new level of the enchantment.
         */
        public void setLevel(int level) {
            this.level = level;
        }

        /**
         * Get the enchantment level by sending an event. Hook to make related
         * patches much cleaner.
         *
         * @param entityLiving The entity for which the enchantment is being
         *        checked.
         * @param enchantment The enchantment for which the level is being
         *        evaluated.
         * @param heldItem The item the entity is currently holding.
         * @param level The current level of the enchantment.
         * @return The new level of the enchantment.
         */
        public static int handle(EntityLivingBase entityLiving, Enchantment enchantment, ItemStack heldItem, int level) {
            final Level event = new Level(entityLiving, enchantment, heldItem, level);
            OpenModLoader.getEventBus().post(event);
            return event.getLevel();
        }
    }
}
