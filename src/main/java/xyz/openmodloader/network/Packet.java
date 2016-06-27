package xyz.openmodloader.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;
import xyz.openmodloader.OpenModLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default OML packet implementation.
 */
public class Packet extends AbstractPacket {

    private final String id;
    final Channel channel;
    final PacketSpec spec;
    final Map<String, Object> values = new HashMap<>();
    final Map<String, DataType> types = new HashMap<>();

    Packet(Channel channel, PacketSpec spec) {
        this.id = spec.name;
        this.channel = channel;
        this.spec = spec;

    }

    /**
     * Stores the given value for the given ID
     * @param id The ID of the thing being stored
     * @param value The value to store
     * @return The current mutable packet builder instance
     */
    public Packet set(String id, Object value) {
        if (!spec.types.containsKey(id)) throw new IllegalArgumentException("No such key " + id);
        if (!spec.types.get(id).getClazz().isAssignableFrom(value.getClass())) throw new IllegalArgumentException(String.format("Key %s expected type %s, got %s", id, spec.types.get(id).getClazz(), value.getClass()));
        if (values.containsKey(id)) throw new IllegalArgumentException(String.format("Key %s is already set to %s", id, values.get(id)));

        values.put(id, value);
        types.put(id, spec.types.get(id));

        return this;
    }

    /**
     * Retrieves a value from this packet
     * @param id The ID of the thing being retrieved
     * @param type The {@link DataType} type of the thing being stored
     * @param <T> The type of the value
     * @return The value stored
     */
    public <T> T get(String id, DataType<T> type) {
        if (!values.containsKey(id)) throw new IllegalArgumentException("No such key " + id);
        if (!types.get(id).equals(type)) throw new IllegalArgumentException(String.format("Wrong type for key %s, %s is registered but got %s", id, types.get(id), type));
        return (T)values.get(id);
    }


    /**
     * Writes this packet to the given buffer
     * @param buf The buffer
     * @return The buffer for convienience
     */
    @Override
    public PacketBuffer write(PacketBuffer buf) {
        values.forEach((id, value) -> {
            types.get(id).write(buf, value);
        });
        return buf;
    }

    /**
     * Reads this packet from the given buffer
     * @param buf The buffer
     */
    @Override
    public void read(PacketBuffer buf) {
        types.forEach((id, type) -> {
            values.put(id, type.read(buf));
        });
    }

    /**
     * Handles receiving this packet
     */
    @Override
    public void handle() {
        spec.handler.accept(new Context(), this);
    }

//	Client -> Server
    /**
     * Sends this packet from the client to the server.
     */
    public void toServer() {
        OpenModLoader.getSidedHandler().getClientPlayer().connection.sendPacket(new PacketWrapper(channel, this));
    }

//	Server -> Client
    /**
     * Sends this packet to the given player
     * @param player The player to send to
     */
    public void toPlayer(EntityPlayerMP player) {
        player.connection.sendPacket(new PacketWrapper(channel, this));
    }

    /**
     * Sends this packet to all the players in the given list
     * @param players The players to send to
     */
    public void toAll(List<EntityPlayerMP> players) {
        PacketWrapper packet = new PacketWrapper(channel, this);
        players.stream()
                .map(player -> player.connection)
                .forEach(connection -> connection.sendPacket(packet));
    }

    /**
     * Sends this packet to all players on the server.
     */
    public void toAll() {
        MinecraftServer server = OpenModLoader.getSidedHandler().getServer();
        toAll(server.getPlayerList().getPlayerList());
    }

    /**
     * Sends this packet to all players in the list where the predicate returns {@code true}
     * @param players All the players to test sending to
     * @param predicate The function to test if the packet should be sent
     */
    public void toAll(List<EntityPlayerMP> players, Predicate<EntityPlayer> predicate) {
        toAll(players.stream()
            .filter(predicate::test)
            .collect(Collectors.toList()));
    }

    /**
     * Sends this packet to all the players in the world within the radius
     * @param world The world
     * @param pos The base position
     * @param radius The radius around the position that players must be in to send the packet to
     */
    public void toAllInRadius(WorldServer world, Vec3d pos, double radius) {
        double maxDistance = radius*radius + radius*radius + radius*radius;
        toAll(world.getPlayers(EntityPlayerMP.class, player -> (player.getDistanceSq(pos.xCoord, pos.yCoord, pos.zCoord) <= maxDistance)));
    }

    /**
     * Sends this packet to all players in the world within the radius
     * @param world The world
     * @param pos The base position
     * @param radius The radius around the position that players must be in to send the packet to
     */
    public void toAllInRadius(WorldServer world, Vec3i pos, double radius) {
        toAllInRadius(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), radius);
    }

    /**
     * Sends this packet to all players in the dimension within the radius
     * @param dimension The dimension
     * @param pos The base position
     * @param radius The radius around the position that players must be in to send the packet to
     */
    public void toAllInRadius(int dimension, Vec3d pos, double radius) {
        MinecraftServer server = OpenModLoader.getSidedHandler().getServer();
        toAllInRadius(server.worldServerForDimension(dimension), pos, radius);
    }

    /**
     * Sends this packet to all players in the dimension within the radius
     * @param dimension The dimension
     * @param pos The base position
     * @param radius The radius around the position that players must be in to send the packet to
     */
    public void toAllInRadius(int dimension, Vec3i pos, double radius) {
        toAllInRadius(dimension, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), radius);
    }

}
