package xyz.openmodloader.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

public class ExplosionEvent extends Event {

    /**
     * The world that the event took place in.
     */
    private final World world;

    /**
     * The source of the explosion.
     */
    private final Entity entity;

    /**
     * The X coordinate of the explosion.
     */
    private double x;

    /**
     * The Y coordinate of the explosion.
     */
    private double y;

    /**
     * The Z coordinate of the explosion.
     */
    private double z;

    /**
     * The size of the explosion.
     */
    private float explosionSize;

    /**
     * Whether or not the explosion on fire.
     */
    private boolean isFlaming;

    /**
     * Whether or not the explosion is smoking.
     */
    private boolean isSmoking;

    /**
     * Constructor for a new event that is fired when an explosion is created.
     *
     * @param world         The world that the explosion is created in.
     * @param entity        The source of the event.
     * @param x             The X coordinate of the explosion.
     * @param y             The Y coordinate of the explosion.
     * @param z             The Z coordinate of the explosion.
     * @param explosionSize The size of the explosion.
     * @param isFlaming     Whether or not the explosion is on fire.
     * @param isSmoking     Whether or not the explosion is smoking.
     */
    public ExplosionEvent(World world, Entity entity, double x, double y, double z, float explosionSize, boolean isFlaming, boolean isSmoking) {
        this.world = world;
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.explosionSize = explosionSize;
        this.isFlaming = isFlaming;
        this.isSmoking = isSmoking;
    }

    /**
     * Gets the world that the explosion was created in.
     *
     * @return The world that the explosion was created in.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets the entity that caused the explosion.
     *
     * @return The entity that caused the explosion.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the X coordinate of the explosion.
     *
     * @return The X coordinate of the explosion.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the X coordinate of the explosion.
     *
     * @param x The new X coordinate of the explosion.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the Y coordinate of the explosion.
     *
     * @return The Y coordinate of the explosion.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the Y coordinate of the explosion.
     *
     * @param y The new Y coordinate of the explosion.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the Z coordinate of the explosion.
     *
     * @return The Z coordinate of the explosion.
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the Z coordinate of the explosion.
     *
     * @param z The new Z coordinate of the explosion.
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Gets the size of the explosion.
     *
     * @return The size of the explosion.
     */
    public float getExplosionSize() {
        return explosionSize;
    }

    /**
     * Sets the size of the explosion.
     *
     * @param explosionSize The size of the explosion.
     */
    public void setExplosionSize(float explosionSize) {
        this.explosionSize = explosionSize;
    }

    /**
     * Checks if the explosion is flaming.
     *
     * @return Whether or not the explosion is flaming.
     */
    public boolean isFlaming() {
        return isFlaming;
    }

    /**
     * Updates the flaming status of the explosion.
     *
     * @param flaming The new flaming status for the explosion.
     */
    public void setFlaming(boolean flaming) {
        isFlaming = flaming;
    }

    /**
     * Checks if the explosion is smoking.
     *
     * @return Whether or not the explosion is smoking.
     */
    public boolean isSmoking() {
        return isSmoking;
    }

    /**
     * Updates the smoking status of the explosion.
     *
     * @param smoking The new smoking status for the explosion.
     */
    public void setSmoking(boolean smoking) {
        isSmoking = smoking;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    /**
     * Hook to make related patches much smaller.
     *
     * @param world         The world that the explosion is created in.
     * @param entity        The source of the event.
     * @param x             The X coordinate of the explosion.
     * @param y             The Y coordinate of the explosion.
     * @param z             The Z coordinate of the explosion.
     * @param explosionSize The size of the explosion.
     * @param isFlaming     Whether or not the explosion is on fire.
     * @param isSmoking     Whether or not the explosion is smoking.
     * @return The explosion to create in the world.
     */
    public static Explosion onExplosion(World world, Entity entity, double x, double y, double z, float explosionSize, boolean isFlaming, boolean isSmoking) {
        ExplosionEvent event = new ExplosionEvent(world, entity, x, y, z, explosionSize, isFlaming, isSmoking);
        return OpenModLoader.INSTANCE.getEventBus().post(event) ? new Explosion(event.world, event.entity, event.x, event.y, event.z, event.explosionSize, event.isFlaming, event.isSmoking) : null;
    }
}