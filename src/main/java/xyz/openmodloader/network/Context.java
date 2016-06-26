package xyz.openmodloader.network;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.strippable.Side;

/**
 * The context in which this packet is being handled
 */
public class Context {

	private final Side side;

	/**
	 * Default context constructor, using the physical side
	 */
	public Context() {
		this(OpenModLoader.INSTANCE.getSidedHandler().getSide());
	}

	public Context(Side side) {
		this.side = side;
	}

	/**
	 * @return The side the packing is being handled on
	 */
	public Side getSide() {
		return side;
	}

}
