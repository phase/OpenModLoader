package xyz.openmodloader.network;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.strippable.Side;

public class Context {

	private final Side side;

	public Context() {
		this(OpenModLoader.INSTANCE.getSidedHandler().getSide());
	}

	public Context(Side side) {
		this.side = side;
	}

	public Side getSide() {
		return side;
	}

}
