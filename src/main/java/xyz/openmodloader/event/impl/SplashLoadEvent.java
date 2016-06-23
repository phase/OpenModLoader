package xyz.openmodloader.event.impl;

import java.util.List;

import xyz.openmodloader.event.Event;

/**
 * Fired while the main Minecraft menu splash text list is being populated.
 * Allows for new splash messages to be added and existing messages to be
 * removed or altered.
 */
public class SplashLoadEvent extends Event {

    /**
     * The list of splash text messages for the main menu to use when deciding a
     * splash text.
     */
    private final List<String> splashTexts;

    /**
     * Constructs an event that is fired when the main menu splash text list is
     * being populated.
     *
     * @param splashTexts
     */
    public SplashLoadEvent(List<String> splashTexts) {
        this.splashTexts = splashTexts;
    }

    /**
     * Gets the list of all current splash text messages. Messages added to this
     * list will have a chance to appear on the main menu as a splash text
     * message. Messages can also be removed.
     *
     * @return The list of splash text messages.
     */
    public List<String> getSplashTexts() {
        return splashTexts;
    }
}
