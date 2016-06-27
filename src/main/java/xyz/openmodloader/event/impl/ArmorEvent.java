package xyz.openmodloader.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

/**
 * Parent class for armor related events. All events that fall within this scope
 * should extend this class. They should also be added as an inner class however
 * this is not required.
 */
public class ArmorEvent extends Event {

    /**
     * The entity that has fired this event
     */
    protected final Entity entity;

    /**
     * The ItemArmor that was used
     */
    protected ItemStack armor;

    /**
     * The armor slot that was used.
     */
    protected EntityEquipmentSlot slot;

    /**
     * Constructor for the base armor events. This constructor should only be
     * accessed through super calls.
     *
     * @param entity The entity that has fired this event.
     * @param armor The ItemArmor that was used
     * @param slot The equipment slot that was used.
     */
    public ArmorEvent(Entity entity, ItemStack armor, EntityEquipmentSlot slot) {
        this.entity = entity;
        this.armor = armor;
        this.slot = slot;
    }

    /**
     * @return the piece of armor that was used.
     */
    public ItemStack getArmor() {
        return armor;
    }

    /**
     * @return the entity that fired the event.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @return the slot that was used.
     */
    public EntityEquipmentSlot getSlot() {
        return slot;
    }

    /**
     * Sets the piece of armor that was used.
     *
     * @param armor The piece of armor that should be used.
     */
    public void setArmor(ItemStack armor) {
        this.armor = armor;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static ArmorEvent handle(Entity entity, ItemStack armor, EntityEquipmentSlot slot) {
        ArmorEvent event;
        if (armor != null) {
            event = new ArmorEvent.Equip(entity, armor, slot);
        } else {
            event = new ArmorEvent.Unequip(entity, armor, slot);
        }
        return OpenModLoader.getEventBus().post(event) ? event : null;
    }

    public static class Equip extends ArmorEvent {

        /**
         * Constructor for this Equip event which is fired when a piece of armor
         * is equipped.
         *
         * @param entity The entity that equipped the armor
         * @param armor The ItemArmor that was equipped
         * @param slot The equipment slot that was equipped
         */
        public Equip(Entity entity, ItemStack armor, EntityEquipmentSlot slot) {
            super(entity, armor, slot);
        }
    }

    public static class Unequip extends ArmorEvent {
        /**
         * Constructor for this Equip event which is fired when a piece of armor
         * is unequipped.
         *
         * @param entity The entity that unequipped the armor
         * @param armor The ItemArmor that was unequipped
         * @param slot The equipment slot that was unequipped
         */
        public Unequip(Entity entity, ItemStack armor, EntityEquipmentSlot slot) {
            super(entity, armor, slot);
        }
    }
}
