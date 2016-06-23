package xyz.openmodloader.event.impl;

import org.lwjgl.input.Keyboard;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

/**
 * An event fired when a key is pressed.
 */
public class KeyPressEvent extends Event {

    /**
     * The character corresponding to the key pressed.
     * @see #getCharPressed()
     */
    private final char charPressed;
    /**
     * The code for the key pressed.
     * @see #getKeyPressed()
     */
    private final int keyPressed;

    public KeyPressEvent(char charPressed, int keyPressed) {
        this.charPressed = charPressed;
        this.keyPressed = keyPressed;
    }

    public char getCharPressed() {
        return charPressed;
    }

    public int getKeyPressed() {
        return keyPressed;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static boolean handle() {
        if (Keyboard.getEventKeyState()) {
            int keyCode = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
            KeyPressEvent event = new KeyPressEvent(Keyboard.getEventCharacter(), keyCode);
            return OpenModLoader.INSTANCE.getEventBus().post(event);
        }
        return true;
    }
}
