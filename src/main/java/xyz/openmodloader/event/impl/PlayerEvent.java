package xyz.openmodloader.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import xyz.openmodloader.OpenModLoader;

/**
 * Parent class for player related events. All events that fall within this
 * scope should extend this class. They should also be added as an inner class
 * however this is not required.
 */
public class PlayerEvent extends EntityEvent {

    /**
     * The player that fired this event.
     */
    protected final EntityPlayer player;

    /**
     * Constructor for the base player events. This constructor should only be accessed through super calls.
     *
     * @param player The player that has fired this event.
     */
    public PlayerEvent(EntityPlayer player) {
        super(player);
        this.player = player;
    }

    /**
     * Gets the player that fired this event.
     *
     * @return the player that fired this event.
     */
    public EntityPlayer getPlayer() {
        return player;
    }

    /**
     * This event is fired when a player crafts an item with a crafting table.
     */
    public static class Craft extends PlayerEvent {

        /**
         * The result from the recipe the player is crafting.
         */
        protected final ItemStack result;

        /**
         * Constructor for the new event that is fired when a player crafts an item with a crafting table.
         *
         * @param player The player that has fired this event.
         * @param result The result from the recipe the player is crafting.
         */
        public Craft(EntityPlayer player, ItemStack result) {
            super(player);
            this.result = result;
        }

        /**
         * Gets the result from the recipe the player is crafting.
         *
         * @return the result from the recipe the player is crafting.
         */
        public ItemStack getResult() {
            return result;
        }
    }

    /**
     * This event is fired when a player picks a smelted item out of a furnace.
     */
    public static class Smelt extends PlayerEvent {

        /**
         * The result from the smelting recipe.
         */
        protected final ItemStack result;

        /**
         * The amount of XP the player should receive.
         */
        protected float xp;

        /**
         * Constructor for the new event that is fired when a player picks a smelted item out of a furnace.
         *
         * @param player The player that has fired this event.
         * @param result The result from the smelting recipe.
         * @param xp The amount of xp to receive.
         */
        public Smelt(EntityPlayer player, ItemStack result, float xp) {
            super(player);
            this.result = result;
        }

        /**
         * Gets the result from the smelting recipe.
         *
         * @return the result from the smelting recipe.
         */
        public ItemStack getResult() {
            return result;
        }

        /**
         * Gets the amount of xp the player should receive.
         *
         * @return the amount of xp the player should receive.
         */
        public float getXP() {
            return xp;
        }

        /**
         * Sets the amount of xp the player should receive.
         *
         * @param xp the amount of xp the player should receive.
         */
        public void setXP(float xp) {
            this.xp = xp;
        }

        /**
         * Hook to make related patches smaller.
         *
         * @param player The player that has fired this event.
         * @param result The result from the smelting recipe.
         * @param xp The amount of xp to receive.
         * @return the amount of to receive.
         */
        public static float handle(EntityPlayer player, ItemStack result, float xp) {
            final PlayerEvent.Smelt event = new PlayerEvent.Smelt(player, result, xp);
            OpenModLoader.getEventBus().post(event);
            return Math.min(1.0F, event.xp);
        }
    }

    /**
     * The base class for entity-tracking related events.
     */
    public static class Track extends PlayerEvent {

        /**
         * The entity being tracked by the player.
         */
        protected final Entity tracking;

        /**
         * Base constructor for entity-tracking related events.
         *
         * @param player the player tracking the entity.
         * @param tracking the entity being tracked.
         */
        public Track(EntityPlayer player, Entity tracking) {
            super(player);
            this.tracking = tracking;
        }

        /**
         * Gets the entity that is being tracked by the player.
         *
         * @return the entity that is being tracked by the player.
         */
        public Entity getTracking() {
            return tracking;
        }

        /**
         * This event is fired when a player starts tracking an entity.
         */
        public static class Start extends Track {

            /**
             * Constructor for new event that is fired when a player starts tracking an entity.
             *
             * @param player the player tracking the entity.
             * @param tracking the entity being tracked.
             */
            public Start(EntityPlayer player, Entity tracking) {
                super(player, tracking);
            }
        }

        /**
         * This event is fired when a player stops tracking an entity.
         */
        public static class Stop extends Track {

            /**
             * Constructor for new event that is fired when a player stops tracking an entity.
             *
             * @param player the player tracking the entity.
             * @param tracking the entity being tracked.
             */
            public Stop(EntityPlayer player, Entity tracking) {
                super(player, tracking);
            }
        }
    }
}
