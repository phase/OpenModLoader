package xyz.openmodloader.event.impl;

import org.lwjgl.input.Keyboard;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

public class InputEvent extends Event {

    /**
     * An event fired whenever the mouse is pressed.
     */
    public static class Mouse extends InputEvent {

        /**
         * The mouse button that was pressed.
         *
         * @see #getButton()
         */
        private final int button;

        public Mouse(int button) {
            this.button = button;
        }

        public int getButton() {
            return button;
        }

        public static boolean handle(int button) {
            InputEvent.Mouse event = new InputEvent.Mouse(button);
            return OpenModLoader.INSTANCE.getEventBus().post(event);
        }
    }

    /**
     * An event fired when a key is pressed.
     */
    public static class Keyboard extends InputEvent {

        /**
         * The character corresponding to the key pressed.
         *
         * @see #getCharacter()
         */
        private final char character;

        /**
         * The code for the key pressed.
         *
         * @see #getKey()
         */
        private final int key;

        public Keyboard(char character, int key) {
            this.character = character;
            this.key = key;
        }

        public char getCharacter() {
            return character;
        }

        public int getKey() {
            return key;
        }

        public static boolean handle() {
            if (org.lwjgl.input.Keyboard.getEventKeyState()) {
                int keyCode = org.lwjgl.input.Keyboard.getEventKey() == 0 ? org.lwjgl.input.Keyboard.getEventCharacter() + 256 : org.lwjgl.input.Keyboard.getEventKey();
                xyz.openmodloader.event.impl.InputEvent.Keyboard event = new xyz.openmodloader.event.impl.InputEvent.Keyboard(org.lwjgl.input.Keyboard.getEventCharacter(), keyCode);
                return OpenModLoader.INSTANCE.getEventBus().post(event);
            }
            return true;
        }
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
