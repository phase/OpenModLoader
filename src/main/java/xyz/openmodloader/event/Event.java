package xyz.openmodloader.event;

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
            throw new RuntimeException("Cannot cancel event " + this);
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
