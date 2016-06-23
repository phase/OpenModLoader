package xyz.openmodloader.event.impl;

import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;
import xyz.openmodloader.event.strippable.Side;

/**
 * An event for when a chat message is received but before it is displayed. This
 * event works on both the client and server.
 */
public class ChatReceivedEvent extends Event {

    /**
     * The message that will be displayed in chat.
     */
    private ITextComponent message;

    /**
     * The side that the event is being fired on. Server/Client
     */
    private final Side side;

    /**
     * Constructs a new event that is fired when a chat message is displayed.
     * 
     * @param message The message that was received.
     * @param side The side that the event is being fired on.
     */
    public ChatReceivedEvent(ITextComponent message, Side side) {
        this.message = message;
        this.side = side;
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

    /**
     * Hook to make related patches much cleaner.
     * 
     * @param message The message that was received.
     * @param side The side that it was received on.
     * @return The message to actually display.
     */
    public static ITextComponent onChatReceived(ITextComponent message, Side side) {
        final ChatReceivedEvent event = new ChatReceivedEvent(message, side);
        return OpenModLoader.INSTANCE.getEventBus().post(event) ? event.getMessage() : null;
    }
}
