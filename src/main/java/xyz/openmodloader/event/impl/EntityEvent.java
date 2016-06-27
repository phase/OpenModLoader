package xyz.openmodloader.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

/**
 * Parent class for entity related events. All events that fall within this
 * scope should extend this class. They should also be added as an inner class
 * however this is not required.
 */
public class EntityEvent extends Event {

    /**
     * The entity that has fired this event.
     */
    protected final Entity entity;

    /**
     * Constructor for the base entity events. This constructor should only be
     * accessed through super calls.
     *
     * @param entity The entity that has fired this event.
     */
    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    /**
     * @return the entity that has fired this event.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * This event is fired when an entity is first constructed via the
     * {@link Entity} constructor.
     */
    public static class Constructing extends EntityEvent {

        /**
         * Constructor for the new event that is fired when an entity is
         * constructed.
         *
         * @param entity The entity being constructed.
         */
        public Constructing(Entity entity) {
            super(entity);
        }
    }

    /**
     * This event is fired when an entity joins the world.
     */
    public static class Join extends EntityEvent {

        /**
         * The world this entity is joining.
         */
        protected final World world;

        /**
         * Constructor for the new event that is fired when an entity joins the
         * world.
         *
         * @param entity The entity joining the world.
         */
        public Join(Entity entity) {
            super(entity);
            this.world = entity.worldObj;
        }

        /**
         * @return the world this entity is joining
         */
        public World getWorld() {
            return world;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    /**
     * This event is fired when an entity moves from one dimension to another.
     */
    public static class ChangeDimension extends EntityEvent {

        /**
         * The dimension this entity is travelling from.
         */
        protected final int previousDimension;

        /**
         * The dimension this entity is travelling to.
         */
        protected int newDimension;

        /**
         * Constructor for the new even that is fired when an entity changes
         * dimensions.
         *
         * @param entity The entity that has fired this event.
         * @param previousDimension The dimension this entity is travelling
         *        from.
         * @param newDimension The dimension this entity is travelling to.
         */
        public ChangeDimension(Entity entity, int previousDimension, int newDimension) {
            super(entity);
            this.previousDimension = previousDimension;
            this.newDimension = newDimension;
        }

        /**
         * Gets the dimension this entity is travelling from.
         *
         * @return the dimension this entity is travelling from.
         */
        public int getPreviousDimension() {
            return previousDimension;
        }

        /**
         * Gets the dimension this entity is travelling to.
         *
         * @return the dimension this entity is travelling to.
         */
        public int getNewDimension() {
            return newDimension;
        }

        /**
         * Sets the dimension this entity will travel to.
         *
         * @param newDimension the new dimension to travel to.
         */
        public void setNewDimension(int newDimension) {
            this.newDimension = newDimension;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }

        /**
         * Hook to make related patches much cleaner.
         *
         * @param entity the entity travelling through dimensions.
         * @param previousDimension the dimension the entity is coming from.
         * @param newDimension the dimension the entity is travelling to.
         *
         * @return the new dimension to travel to.
         */
        public static int handle(Entity entity, int previousDimension, int newDimension) {
            final EntityEvent.ChangeDimension event = new EntityEvent.ChangeDimension(entity, previousDimension, newDimension);
            OpenModLoader.INSTANCE.getEventBus().post(event);
            return event.isCanceled() ? event.previousDimension : event.newDimension;
        }
    }
}
