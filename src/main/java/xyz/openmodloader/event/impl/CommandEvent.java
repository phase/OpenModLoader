package xyz.openmodloader.event.impl;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

/**
 * An event for when a command is run by either a player or the console. This
 * event works on both the client and server.
 */
public class CommandEvent extends Event {

    /**
     * The command that is run
     */
    private ICommand command;

    /**
     * The ICommandSender who ran the command
     */
    private ICommandSender sender;
    /**
     * The list of arguments used in the command
     */
    private String[] args;

    /**
     * Constructs a new event that is fired when a command is ran.
     *
     * @param args The Arguments sent
     * @param command The command ran
     * @param sender The person who ran the command
     */
    public CommandEvent(String[] args, ICommand command, ICommandSender sender) {
        this.args = args;
        this.command = command;
        this.sender = sender;
    }

    /**
     * Gets the arguments that were used
     *
     * @return The arguments used to run the command
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Gets the command that was run
     *
     * @return The Command that was run
     */
    public ICommand getCommand() {
        return command;
    }

    /**
     * Gets the sender who ran the command
     *
     * @return The sender who ran the command
     */
    public ICommandSender getSender() {
        return sender;
    }

    /**
     * Sets the arguments to be used in the commands
     *
     * @param args The arguments to use
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    /**
     * Hook to make related patches much cleaner.
     *
     * @param args The arguments used in the command
     * @param command The command ran
     * @param sender The sender who ran the command
     * @return The message to actually display.
     */
    public static String[] handle(String[] args, ICommand command, ICommandSender sender) {
        final CommandEvent event = new CommandEvent(args, command, sender);
        return OpenModLoader.INSTANCE.getEventBus().post(event) ? event.getArgs() : null;
    }
}
