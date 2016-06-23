package xyz.openmodloader.event;

import xyz.openmodloader.OpenModLoader;

public class Event {

    /**
     * Whether or not the event has been canceled.
     */
    private boolean canceled;

    /**
     * Checks if the event can be canceled.
     * 
     * @return Whether or not the event can be canceled.
     */
    public boolean isCancelable() {
        return false;
    }

    /**
     * Updated the canceled status of the event. True means canceled, false
     * means not canceled.
     * 
     * @param canceled The new canceled status for the event.
     */
    public void setCanceled(boolean canceled) {
        if (!this.isCancelable()) {
            OpenModLoader.INSTANCE.LOGGER.warn("An attempt was made to cancel the " + this.getClass().getName() + " event, but that event can not be canceled!");
        }
        this.canceled = canceled;
    }

    /**
     * Checks if the event has been canceled.
     * 
     * @return Whether or not the event has been canceled.
     */
    public boolean isCanceled() {
        return canceled;
    }
}
