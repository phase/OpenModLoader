package xyz.openmodloader.event.impl;

import net.minecraft.world.biome.Biome;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;

/**
 * Parent class for all events that fit under the scope of biomes.
 */
public class BiomeEvent extends Event {

    /**
     * The biome at the center of the event.
     */
    private final Biome biome;

    /**
     * Constructor for the parent biome event.
     * 
     * @param biome The biome to create the event for.
     */
    private BiomeEvent(Biome biome) {
        this.biome = biome;
    }

    /**
     * Gets the event that the event was fired for.
     * 
     * @return The biome that the event was fired for.
     */
    public Biome getBiome() {
        return this.biome;
    }

    /**
     * Parent event for all events that fit the scope of biome colors.
     */
    @Strippable(side = Side.CLIENT)
    public static class BiomeColor extends BiomeEvent {

        /**
         * The original color modifier.
         */
        private final int originalModifier;

        /**
         * The color modifier to actually use. By default this will be the same
         * as the original modifier.
         */
        private int colorModifier;

        /**
         * Constructor for the parent biome color event.
         * 
         * @param biome The event to get the color for.
         * @param originalModifier The original color modifier.
         */
        private BiomeColor(Biome biome, int originalModifier) {
            super(biome);
            this.originalModifier = originalModifier;
            this.colorModifier = originalModifier;
        }

        /**
         * Gets the original color modifier.
         * 
         * @return The original color modifier.
         */
        public int getOriginalColor() {
            return this.originalModifier;
        }

        /**
         * Gets the color modifier to actually use.
         * 
         * @return The color modifier to actually use.
         */
        public int getColorModifier() {
            return colorModifier;
        }

        /**
         * Gets the color modifier to actually use.
         * 
         * @param colorModifier The color modifier to actually use.
         */
        public void setColorModifier(int colorModifier) {
            this.colorModifier = colorModifier;
        }

        /**
         * A hook method to fire a color event, and get the color as a result.
         * 
         * @param color The color event to fire.
         * @return The color to use.
         */
        public static int handle(BiomeColor color) {
            OpenModLoader.INSTANCE.getEventBus().post(color);
            return color.getColorModifier();
        }
    }

    /**
     * Fired when the game tries to get the foliage color for a biome.
     */
    @Strippable(side = Side.CLIENT)
    public static class Foliage extends BiomeColor {

        /**
         * Constructs an event that is fired when the game gets the foliage
         * color for a biome.
         * 
         * @param biome The biome to get the foliage color for.
         * @param original The original foliage color.
         */
        public Foliage(Biome biome, int original) {
            super(biome, original);
        }
    }

    /**
     * Fired when the game tries to get the water color for a biome.
     */
    @Strippable(side = Side.CLIENT)
    public static class Water extends BiomeColor {

        /**
         * Constructs an event that is fired when the game gets the water color
         * for a biome.
         * 
         * @param biome The biome to get the water color for.
         * @param original The original water color.
         */
        public Water(Biome biome, int original) {
            super(biome, original);
        }
    }

    /**
     * Fired when the game tries to get the grass color for a biome.
     */
    @Strippable(side = Side.CLIENT)
    public static class Grass extends BiomeColor {

        /**
         * Constructs an event that is fired when the game gets the grass color
         * for a biome.
         * 
         * @param biome The biome to get the grass color for.
         * @param original The original grass color.
         */
        public Grass(Biome biome, int original) {
            super(biome, original);
        }
    }
}
