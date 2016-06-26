package xyz.openmodloader.event.impl;

import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;
import xyz.openmodloader.launcher.strippable.Side;

public class MessageEvent extends Event {
    /**
     * The message that will be displayed in chat.
     */
    private ITextComponent message;

    /**
     * The side that the event is being fired on. Server/Client
     */
    private final Side side;

    public MessageEvent(ITextComponent message, Side side) {
        this.message = message;
        this.side = side;
    }

    public static class Chat extends MessageEvent {

        /**
         * Constructs a new event that is fired when a chat message is displayed.
         *
         * @param message The message that was received.
         * @param side    The side that the event is being fired on.
         */
        public Chat(ITextComponent message, Side side) {
            super(message, side);
        }

        /**
         * Hook to make related patches much cleaner.
         *
         * @param message The message that was received.
         * @param side    The side that it was received on.
         * @return The message to actually display.
         */
        public static ITextComponent handle(ITextComponent message, Side side) {
            final MessageEvent.Chat event = new MessageEvent.Chat(message, side);
            return OpenModLoader.INSTANCE.getEventBus().post(event) ? event.getMessage() : null;
        }
    }

    public static class Snackbar extends MessageEvent {

        /**
         * Constructs a new event that is fired when a chat message is displayed.
         *
         * @param message The message that was received.
         * @param side    The side that the event is being fired on.
         */
        public Snackbar(ITextComponent message, Side side) {
            super(message, side);
        }

        /**
         * Hook to make related patches much cleaner.
         *
         * @param message The message that was received.
         * @param side    The side that it was received on.
         * @return The message to actually display.
         */
        public static ITextComponent handle(ITextComponent message, Side side) {
            final MessageEvent.Snackbar event = new MessageEvent.Snackbar(message, side);
            return OpenModLoader.INSTANCE.getEventBus().post(event) ? event.getMessage() : null;
        }
    }

    /**
     * Gets the message that was received.
     *
     * @return The message that was received.
     */
    public ITextComponent getMessage() {
        return message;
    }

    /**
     * Sets the message to display in chat to a new one.
     *
     * @param message The new message to display.
     */
    public void setMessage(ITextComponent message) {
        this.message = message;
    }

    /**
     * Gets the side that the event is being fired on. Allows for easy
     * differentiation between client and server.
     *
     * @return The side where the event was fired.
     */
    public Side getSide() {
        return side;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
