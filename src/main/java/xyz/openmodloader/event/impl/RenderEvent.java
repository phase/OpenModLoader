package xyz.openmodloader.event.impl;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

public class RenderEvent extends Event {

    /**
     * An event that is fired any time an entity is rendered.
     */
    public static class Entities extends RenderEvent {

        /**
         * The Render being rendered.
         */
        private final Render<?> render;

        /**
         * The Entity being rendered.
         */
        private final Entity entity;

        /**
         * The X coordinate for the render.
         */
        private final double x;

        /**
         * The Y coordinate for the render.
         */
        private final double y;

        /**
         * The Z coordinate for the render.
         */
        private final double z;

        /**
         * The yaw of the entity being rendered.
         */
        private final float yaw;

        /**
         * The current partial ticks.
         */
        private final float partialTicks;

        /**
         * Constructor for an event that is fired when any entity is rendered.
         * 
         * @param render The render being rendered.
         * @param entity The entity being rendered.
         * @param x The X coordinate for the render
         * @param y The Y coordinate for the render
         * @param z The Z coordinate for the render.
         * @param yaw The yaw of the entity being rendered.
         * @param partialTicks The current partial ticks.
         */
        public Entities(Render<?> render, Entity entity, double x, double y, double z, float yaw, float partialTicks) {

            this.render = render;
            this.entity = entity;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.partialTicks = partialTicks;
        }

        /**
         * Gets the X coordinate for the render.
         * 
         * @return The X coordinate for the render.
         */
        public double getX() {
            return x;
        }

        /**
         * Gets the Y coordinate for the render.
         * 
         * @return The Y coordinate for the render.
         */
        public double getY() {
            return y;
        }

        /**
         * Gets the Z coordinate for the render.
         * 
         * @return The Z coordinate for the render.
         */
        public double getZ() {
            return z;
        }

        /**
         * Gets the yaw of the entity being rendered.
         * 
         * @return The yaw of the entity being rendered.
         */
        public float getYaw() {
            return yaw;
        }

        /**
         * Gets the current partial ticks.
         * 
         * @return The current partial ticks.
         */
        public float getPartialTicks() {
            return partialTicks;
        }

        /**
         * Gets the Render being rendered.
         * 
         * @return The Render being rendered.
         */
        public Render<?> getRender() {
            return render;
        }

        /**
         * Gets the entity being rendered.
         * 
         * @return The entity being rendered.
         */
        public Entity getEntity() {
            return entity;
        }

        /**
         * A hook to make related patches much smaller.
         * 
         * @param render The render being rendered.
         * @param entity The entity being rendered.
         * @param x The X coordinate for the render
         * @param y The Y coordinate for the render
         * @param z The Z coordinate for the render.
         * @param yaw The yaw of the entity being rendered.
         * @param partialTicks The current partial ticks.
         * @return Whether or not the event was successful.
         */
        public static boolean onRender(Render<?> render, Entity entity, double x, double y, double z, float yaw, float partialTicks) {
            GlStateManager.pushMatrix();
            final boolean successful = OpenModLoader.getEventBus().post(new Entities(render, entity, x, y, z, yaw, partialTicks));
            GlStateManager.popMatrix();
            return successful;
        }
    }

    /**
     * An event that is fired any time a render layer is rendered.
     */
    public static class Layers extends RenderEvent {

        /**
         * The layer being rendered.
         */
        private final LayerRenderer<?> layer;

        /**
         * The entity that the layer is being rendered on.
         */
        private final EntityLivingBase entity;

        /**
         * The angle of the entities limb being swung.
         */
        private final float limbSwing;

        /**
         * The amount of entities limb being swung.
         */
        private final float limbSwingAmount;

        /**
         * The current partial ticks.
         */
        private final float partialTicks;

        /**
         * The age in ticks.
         */
        private final float tickAge;

        /**
         * The yaw of the entities head.
         */
        private final float headYaw;

        /**
         * The pitch of the entities head.
         */
        private final float headPitch;

        /**
         * The scale of the layer.
         */
        private final float scale;

        /**
         * A new event that is fired every time a layer is rendered.
         * 
         * @param layer The layer being rendered.
         * @param entity The entity that the layer is being rendered on.
         * @param limbSwing The angle of the entities limbs being swung.
         * @param limbSwingAmount The amount of the entities limbs being swung.
         * @param partialTicks The current partial ticks.
         * @param tickAge The age in ticks.
         * @param headYaw The yaw of the entities head.
         * @param headPitch The pitch of the entities head.
         * @param scale The scale of the layer;
         */
        public Layers(LayerRenderer<?> layer, EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float tickAge, float headYaw, float headPitch, float scale) {

            this.layer = layer;
            this.entity = entity;
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.partialTicks = partialTicks;
            this.tickAge = tickAge;
            this.headYaw = headYaw;
            this.headPitch = headPitch;
            this.scale = scale;
        }

        /**
         * Gets the layer being rendered.
         * 
         * @return The layer being rendered.
         */
        public LayerRenderer<?> getLayer() {
            return layer;
        }

        /**
         * Gets the entity that the layer is being rendered on.
         * 
         * @return The entity that the layer is being rendered on.
         */
        public EntityLivingBase getEntity() {
            return entity;
        }

        /**
         * Gets the angle of the entities limbs being swung.
         * 
         * @return The angle of the entities limbs being swung.
         */
        public float getLimbSwing() {
            return limbSwing;
        }

        /**
         * Gets the amount of the entities limbs being swung.
         * 
         * @return The amount of the entities limbs being swung.
         */
        public float getLimbSwingAmount() {
            return limbSwingAmount;
        }

        /**
         * Gets the current partial ticks.
         * 
         * @return The current partial ticks.
         */
        public float getPartialTicks() {
            return partialTicks;
        }

        /**
         * Gets the age in ticks.
         * 
         * @return The age in ticks.
         */
        public float getTickAge() {
            return tickAge;
        }

        /**
         * Gets the yaw of the entities head.
         * 
         * @return The yaw of the entities head.
         */
        public float getHeadYaw() {
            return headYaw;
        }

        /**
         * Gets the pitch of the entities head.
         * 
         * @return The pitch of the entities head.
         */
        public float getHeadPitch() {
            return headPitch;
        }

        /**
         * Gets the scale of the layer.
         * 
         * @return The scale of the layer.
         */
        public float getScale() {
            return scale;
        }

        /**
         * A hook to make related patches much smaller.
         * 
         * @param layer The layer being rendered.
         * @param entity The entity that the layer is being rendered on.
         * @param limbSwing The angle of the entities limbs being swung.
         * @param limbSwingAmount The amount of the entities limbs being swung.
         * @param partialTicks The current partial ticks.
         * @param tickAge The age in ticks.
         * @param headYaw The yaw of the entities head.
         * @param headPitch The pitch of the entities head.
         * @param scale The scale of the layer;
         * @return Whether or not the event was successful.
         */
        public static boolean onRender(LayerRenderer<?> layer, EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float tickAge, float headYaw, float headPitch, float scale) {
            GlStateManager.pushMatrix();
            final boolean successful = OpenModLoader.getEventBus().post(new Layers(layer, entity, limbSwing, limbSwingAmount, partialTicks, tickAge, headYaw, headPitch, scale));
            GlStateManager.popMatrix();
            return successful;
        }
    }

    /**
     * An event that is fired every time a shadow is rendered.
     */
    public static class Shadows extends RenderEvent {

        /**
         * The entity that is having their shadow rendered.
         */
        private final Entity entity;

        /**
         * The X coordinate for the render.
         */
        private final double x;

        /**
         * The Y coordinate for the render.
         */
        private final double y;

        /**
         * The Z coordinate for the render.
         */
        private final double z;

        /**
         * The yaw of the entity that is having their shadow rendered.
         */
        private final float yaw;

        /**
         * The current partial ticks.
         */
        private final float partialTicks;

        /**
         * Constructs an event that is fired every time an entities shadow is
         * rendered.
         * 
         * @param entity The entity that is having their shadow rendered.
         * @param x The X coordinate for the render.
         * @param y The Y coordinate for the render.
         * @param z The Z coordinate for the render.
         * @param yaw The yaw of the entity that is having their shadow
         *        rendered.
         * @param partialTicks The current partial ticks.
         */
        public Shadows(Entity entity, double x, double y, double z, float yaw, float partialTicks) {

            this.entity = entity;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.partialTicks = partialTicks;
        }

        /**
         * Gets the entity that is having their shadow rendered.
         * 
         * @return The entity that is having their shadow rendered.
         */
        public Entity getEntity() {
            return entity;
        }

        /**
         * Gets the X coordinate for the render.
         * 
         * @return The X coordinate for the render.
         */
        public double getX() {
            return x;
        }

        /**
         * Gets the Y coordinate for the render.
         * 
         * @return The Y coordinate for the render.
         */
        public double getY() {
            return y;
        }

        /**
         * Gets the Z coordinate for the render.
         * 
         * @return The Z coordinate for the render.
         */
        public double getZ() {
            return z;
        }

        /**
         * Gets the yaw of the entity that is having their shadow rendered.
         * 
         * @return The yaw of the entity that is having their shadow rendered.
         */
        public float getYaw() {
            return yaw;
        }

        /**
         * Gets the current partial ticks.
         * 
         * @return The current partial ticks.
         */
        public float getPartialTicks() {
            return partialTicks;
        }

        /**
         * A hook to make related patches much smaller.
         * 
         * @param entity The entity that is having their shadow rendered.
         * @param x The X coordinate for the render.
         * @param y The Y coordinate for the render.
         * @param z The Z coordinate for the render.
         * @param yaw The yaw of the entity that is having their shadow
         *        rendered.
         * @param partialTicks The current partial ticks.
         * @return Whether or not the event was successful.
         */
        public static boolean onRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
            GlStateManager.pushMatrix();
            final boolean successful = OpenModLoader.getEventBus().post(new Shadows(entity, x, y, z, yaw, partialTicks));
            GlStateManager.popMatrix();
            return successful;
        }
    }

    /**
     * An event that is fired every time entity fire is rendered.
     */
    public static class Fires extends RenderEvent {

        /**
         * The entity that is having fire rendered.
         */
        private final Entity entity;

        /**
         * The X coordinate for the render.
         */
        private final double x;

        /**
         * The Y coordinate for the render.
         */
        private final double y;

        /**
         * The Z coordinate for the render.
         */
        private final double z;

        /**
         * The yaw of the entity that is having their shadow rendered.
         */
        private final float yaw;

        /**
         * The current partial ticks.
         */
        private final float partialTicks;

        /**
         * Constructs an event that is fired every time entity fire is rendered.
         * 
         * @param entity The entity that is having their shadow rendered.
         * @param x The X coordinate for the render.
         * @param y The Y coordinate for the render.
         * @param z The Z coordinate for the render.
         * @param yaw The yaw of the entity that is having their shadow
         *        rendered.
         * @param partialTicks The current partial ticks.
         */
        public Fires(Entity entity, double x, double y, double z, float yaw, float partialTicks) {

            this.entity = entity;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.partialTicks = partialTicks;
        }

        /**
         * Gets the entity that is having their fire rendered.
         * 
         * @return The entity that is having their fire rendered.
         */
        public Entity getEntity() {
            return entity;
        }

        /**
         * Gets the X coordinate for the render.
         * 
         * @return The X coordinate for the render.
         */
        public double getX() {
            return x;
        }

        /**
         * Gets the Y coordinate for the render.
         * 
         * @return The Y coordinate for the render.
         */
        public double getY() {
            return y;
        }

        /**
         * Gets the Z coordinate for the render.
         * 
         * @return The Z coordinate for the render.
         */
        public double getZ() {
            return z;
        }

        /**
         * Gets the yaw of the entity that is having their fire rendered.
         * 
         * @return The yaw of the entity that is having their fire rendered.
         */
        public float getYaw() {
            return yaw;
        }

        /**
         * Gets the current partial ticks.
         * 
         * @return The current partial ticks.
         */
        public float getPartialTicks() {
            return partialTicks;
        }

        /**
         * A hook to make related patches much smaller.
         * 
         * @param entity The entity that is having their fire rendered.
         * @param x The X coordinate for the render.
         * @param y The Y coordinate for the render.
         * @param z The Z coordinate for the render.
         * @param yaw The yaw of the entity that is having their shadow
         *        rendered.
         * @param partialTicks The current partial ticks.
         * @return Whether or not the event was successful.
         */
        public static boolean onRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
            GlStateManager.pushMatrix();
            final boolean successful = OpenModLoader.getEventBus().post(new Fires(entity, x, y, z, yaw, partialTicks));
            GlStateManager.popMatrix();
            return successful;
        }
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}