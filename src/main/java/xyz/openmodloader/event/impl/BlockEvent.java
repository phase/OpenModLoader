package xyz.openmodloader.event.impl;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

import java.util.List;

/**
 * Parent class for block related events. All events that fall within this scope
 * should extend this class. They should also be added as an inner class however
 * this is not required.
 */
public class BlockEvent extends Event {

    /**
     * The world where the event took place.
     */
    protected World world;

    /**
     * The state of the block involved with the event.
     */
    protected IBlockState state;

    /**
     * The position of the event.
     */
    protected BlockPos pos;

    /**
     * Constructor for the base block event. This constructor should only be
     * accessed through super calls.
     *
     * @param world The world where the event took place.
     * @param state The state of the block involved with the event.
     * @param pos   The position of the event.
     */
    public BlockEvent(World world, IBlockState state, BlockPos pos) {
        this.world = world;
        this.state = state;
        this.pos = pos;
    }

    /**
     * An event that is fired just before a block is placed.
     */
    public static class Place extends BlockEvent {

        /**
         * Constructor for a new event that is fired when a block is placed.
         *
         * @param world The world where the event took place.
         * @param state The state of the block involved with the event.
         * @param pos   The position of the event.
         */
        public Place(World world, IBlockState state, BlockPos pos) {
            super(world, state, pos);
        }

        /**
         * Sets the state to block state to a new one.
         *
         * @param state The new state for the block.
         */
        public void setBlockState(IBlockState state) {
            this.state = state;
        }

        /**
         * Hook to make related patches much cleaner.
         *
         * @param world The world where the event took place.
         * @param state The state of the block involved with the event.
         * @param pos   The position of the event.
         * @return The state for the block to be placed.
         */
        public static IBlockState handle(World world, IBlockState state, BlockPos pos) {
            xyz.openmodloader.event.impl.BlockEvent.Place event = new xyz.openmodloader.event.impl.BlockEvent.Place(world, state, pos);
            return OpenModLoader.INSTANCE.getEventBus().post(event) ? event.getBlockState() : null;
        }
    }

    /**
     * An event that is fired just before a block is destroyed.
     */
    public static class Destroy extends BlockEvent {

        /**
         * Constructor for a new event that is fired when a block is destroyed.
         *
         * @param world The world where the event took place.
         * @param state The state of the block involved with the event.
         * @param pos   The position of the event.
         */
        public Destroy(World world, IBlockState state, BlockPos pos) {
            super(world, state, pos);
        }
    }

    /**
     * An event that is fired while a player mines a block. It is specifically
     * focused on the speed of which the block is mined.
     */
    public static class DigSpeed extends BlockEvent {

        /**
         * The speed that the block will be mined.
         */
        private float digSpeed;

        /**
         * Constructor for a new event that is fired while a block is being
         * mined.
         *
         * @param digSpeed The speed that the block is being mined at.
         * @param world    The world where the event took place.
         * @param state    The state of the block involved with the event.
         * @param pos      The position of the event.
         */
        public DigSpeed(float digSpeed, World world, IBlockState state, BlockPos pos) {
            super(world, state, pos);
            this.digSpeed = digSpeed;
        }

        /**
         * Gets the speed that the the block is being mined at.
         *
         * @return The speed the block is being mined at.
         */
        public float getDigSpeed() {
            return digSpeed;
        }

        /**
         * Sets the speed that the block should be mined at.
         *
         * @param digSpeed The new dig speed.
         */
        public void setDigSpeed(float digSpeed) {
            this.digSpeed = digSpeed;
        }

        /**
         * Hook to make related patches much cleaner.
         *
         * @param digSpeed The speed at which the block is being mined.
         * @param world    The world where the event took place.
         * @param state    The state of the block involved with the event.
         * @param pos      The position of the event.
         * @return The state for the block to be placed.
         */
        public static float handle(float digSpeed, World world, IBlockState state, BlockPos pos) {
            DigSpeed event = new DigSpeed(digSpeed, world, state, pos);
            return !OpenModLoader.INSTANCE.getEventBus().post(event) || event.getDigSpeed() < 0F ? 0f : event.getDigSpeed();
        }
    }

    /**
     * An event that is fired just after a block is mined to collect the drops of the block
     * This event is only called in server side
     */
    public static class HarvestDrops extends BlockEvent {

        /**
         * The probability to drop one item when the block is mined
         */
        private float chance;

        /**
         * The level of the fortune enchantment in the tool used to mine the block
         */
        private int fortune;

        /**
         * The list of items that the block will drop
         */
        private List<ItemStack> drops;

        /**
         * Constructor for the base block event. This constructor should only be
         * accessed through super calls.
         *
         * @param world   The world where the event took place.
         * @param state   The state of the block involved with the event.
         * @param pos     The position of the event.
         * @param chance  The probability to drop one item
         * @param fortune The level of fortune used to mine the block
         * @param drops   The list of items that the block will drop
         */
        public HarvestDrops(World world, IBlockState state, BlockPos pos, float chance, int fortune, List<ItemStack> drops) {
            super(world, state, pos);
            this.chance = chance;
            this.fortune = fortune;
            this.drops = drops;
        }

        /**
         * Gets the probability to drop one item from the drop list
         *
         * @return The chance to drop one item
         */
        public float getChance() {
            return chance;
        }

        /**
         * Gets the fortune level of the tool used to mine the block
         *
         * @return The fortune level used to mine the block
         */
        public int getFortune() {
            return fortune;
        }

        /**
         * Gets the list of item that the block will drop
         *
         * @return The list of drops
         */
        public List<ItemStack> getDrops() {
            return drops;
        }

        /**
         * Hook to make related patches much cleaner.
         *
         * @param world   The world where the event took place.
         * @param state   The state of the block involved with the event.
         * @param pos     The position of the event.
         * @param chance  The probability to drop one item
         * @param fortune The level of fortune in the tool used to mine the block
         * @param drops   The list of drops that the block can drop
         * @return The list of items that the block will drop
         */
        public static List<ItemStack> handle(World world, IBlockState state, BlockPos pos, float chance, int fortune, List<ItemStack> drops) {
            BlockEvent.HarvestDrops event = new xyz.openmodloader.event.impl.BlockEvent.HarvestDrops(world, state, pos, chance, fortune, ImmutableList.copyOf(drops));
            OpenModLoader.INSTANCE.getEventBus().post(event);
            return event.getDrops();
        }
    }

    /**
     * Gets the world that the event took place in.
     *
     * @return The world where the event took place.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets the state of the block involved with the event.
     *
     * @return The state of the block involved with the event.
     */
    public IBlockState getBlockState() {
        return state;
    }

    /**
     * Gets the position of the event.
     *
     * @return The position of the event.
     */
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
