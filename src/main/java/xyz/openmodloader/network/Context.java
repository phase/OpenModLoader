package xyz.openmodloader.network;

import net.minecraft.entity.player.EntityPlayer;
import xyz.openmodloader.event.strippable.Side;

public class Context {

	private final Side side;
	private final EntityPlayer player;

	public Context(Side side, EntityPlayer player) {
		this.side = side;
		this.player = player;
	}

	public Side getSide() {
		return side;
	}

	public EntityPlayer getPlayer() {
		return player;
	}
}
