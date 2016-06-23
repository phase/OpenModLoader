package xyz.openmodloader.event.impl;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

/**
 * An event fired whenever the mouse is pressed.
 */
public class MouseClickEvent extends Event {

    /**
     * The mouse button that was pressed.
     *
     * @see #getButton()
     */
    private final int button;

    public MouseClickEvent(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static boolean handle(int button) {
        MouseClickEvent event = new MouseClickEvent(button);
        return OpenModLoader.INSTANCE.getEventBus().post(event);
    }
}
