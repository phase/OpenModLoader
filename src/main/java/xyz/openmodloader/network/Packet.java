package xyz.openmodloader.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.server.OMLServerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Packet implements IPacket {

	private final String id;
	final Channel channel;
	final PacketSpec spec;
	final Map<String, Object> values = new HashMap<>();
	final Map<String, DataType> types = new HashMap<>();

	public Packet(Channel channel, PacketSpec spec) {
		this.id = spec.id;
		this.channel = channel;
		this.spec = spec;

	}

	public Packet set(String id, Object value) {
		if (!spec.types.containsKey(id)) throw new IllegalArgumentException("No such key " + id);
		if (!spec.types.get(id).getClazz().isAssignableFrom(value.getClass())) throw new IllegalArgumentException(String.format("Key %s expected type %s, got %s", id, spec.types.get(id).getClazz(), value.getClass()));
		if (values.containsKey(id)) throw new IllegalArgumentException(String.format("Key %s is already set to %s", id, values.get(id)));

		values.put(id, value);
		types.put(id, spec.types.get(id));

		return this;
	}

	public <T> T get(String id, DataType<T> type) {
		if (!values.containsKey(id)) throw new IllegalArgumentException("No such key " + id);
		if (!types.get(id).equals(type)) throw new IllegalArgumentException(String.format("Wrong type for key %s, %s is registered but got %s", id, types.get(id), type));
		return (T)values.get(id);
	}

	@Override
	public PacketBuffer write(PacketBuffer buf) {

		values.forEach((id, value) -> {
			types.get(id).write(buf, value);
		});
		return buf;
	}

	@Override
	public void read(PacketBuffer buf) {
		types.forEach((id, type) -> {
			values.put(id, type.read(buf));
		});
	}

	@Override
	public void handle() {
		spec.handler.accept(new Context(), this);
	}

//	Client -> Server
	public void toServer() {
		OpenModLoader.INSTANCE.getSidedHandler().getClientPlayer().connection.sendPacket(new PacketWrapper(channel, this));
	}

//	Server -> Client
	public void toPlayer(EntityPlayerMP player) {
		player.connection.sendPacket(new PacketWrapper(channel, this));
	}

	public void toAll(List<EntityPlayerMP> players) {
		PacketWrapper packet = new PacketWrapper(channel, this);
		players.stream()
				.map(player -> player.connection)
				.forEach(connection -> connection.sendPacket(packet));
	}

	public void toAll() {
		MinecraftServer server = OMLServerHelper.getServer();
		toAll(server.getPlayerList().getPlayerList());
	}

	public void toAll(List<EntityPlayerMP> players, Predicate<EntityPlayer> predicate) {
		toAll(players.stream()
			.filter(predicate::test)
			.collect(Collectors.toList()));
	}

	public void toAllInRadius(WorldServer world, Vec3d pos, double radius) {
		double maxDistance = radius*radius + radius*radius + radius*radius;
		toAll(world.getPlayers(EntityPlayerMP.class, player -> (player.getDistanceSq(pos.xCoord, pos.yCoord, pos.zCoord) <= maxDistance)));
	}

	public void toAllInRadius(WorldServer world, Vec3i pos, double radius) {
		toAllInRadius(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), radius);
	}

	public void toAllInRadius(int dimension, Vec3d pos, double radius) {
		MinecraftServer server = OMLServerHelper.getServer();
		toAllInRadius(server.worldServerForDimension(dimension), pos, radius);
	}

	public void toAllInRadius(int dimension, Vec3i pos, double radius) {
		toAllInRadius(dimension, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), radius);
	}

}
