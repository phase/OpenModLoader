package xyz.openmodloader.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.network.PacketBuffer;

/**
 * Default OML channel implementation.
 */
public class Channel extends AbstractChannel<Packet> {

    final String id;
    private final BiMap<String, PacketSpec> specs;
    private final BiMap<String, Integer> specIDs;

    Channel(String id) {
        this.id = id;
        this.specs = HashBiMap.create();
        this.specIDs = HashBiMap.create();
    }

    private Channel(Channel channel) {
        this.id = channel.id;
        this.specs = ImmutableBiMap.copyOf(channel.specs);
        this.specIDs = ImmutableBiMap.copyOf(channel.specIDs);
    }

    /**
     * Creates a new packet spec used for building.
     * @param name The ID of the new packet.
     * @return The new packet spec to be used for building the spec.
     */
    public PacketSpec createPacket(String name) {
        return new PacketSpec(this, name);
    }

    void addPacket(PacketSpec spec) {
        specs.put(spec.name, spec);
    }

    /**
     * Finalize this channel.
     * This creates an immutable copy of this builder channel which is then registered with the {@link ChannelManager}
     * @return The finalized channel
     */
    public Channel build() {
        Channel immutable = new Channel(this);
        ChannelManager.register(id, immutable);
        return immutable;
    }

    /**
     * Creates a packet builder for sending a packet
     * @param id The ID of the packet to create the builder for
     * @return The builder packet
     */
    public Packet send(String id) {
        if (!specs.containsKey(id)) throw new IllegalArgumentException("Invalid PacketSpec name " + id);
        return new Packet(this, specs.get(id));
    }

    /**
     * Writes the given packet to the buffer
     * @param buf The buffer to write to
     * @param packet The packet to write
     */
    @Override
    public void write(PacketBuffer buf, Packet packet) {
        buf.writeInt(getID(packet.spec));
    }

    /**
     * Reads a packet from the given buffer
     * @param buf The buffer to read from
     * @return The packet that was read
     */
    @Override
    public Packet read(PacketBuffer buf) {
        PacketSpec spec = getSpec(buf.readInt());
        Packet packet = new Packet(this, spec);
        packet.read(buf);
        return packet;
    }

    /**
     * Handles a packet
     * @param packet The packet to handle
     */
    @Override
    public void handle(Packet packet) {
        packet.handle();
    }

    /**
     * Gets the packet specification corresponding to the given name.
     * @param name The name of the spec
     * @return The packet specification
     */
    public PacketSpec getSpec(String name) {
        return specs.get(name);
    }

    /**
     * Gets the packet specification corresponding to the given ID
     * @param id The ID of the spec
     * @return The packet specification
     */
    public PacketSpec getSpec(int id) {
        return specs.get(getName(id));
    }

    /**
     * Gets the name corresponding to the given packet specification
     * @param spec The spec
     * @return The name
     */
    public String getName(PacketSpec spec) {
        return specs.inverse().get(spec);
    }

    /**
     * Gets the name corresponding to the packet specification with the given ID
     * @param id The ID of the spec
     * @return The name
     */
    public String getName(int id) {
        return specIDs.inverse().get(id);
    }

    /**
     * Gets the ID corresponding to the given packet specification
     * @param spec The spec
     * @return The ID
     */
    public int getID(PacketSpec spec) {
        return specIDs.get(getName(spec));
    }

    /**
     * Gets the ID corresponding to the packet specification with the given name
     * @param name The name of the spec
     * @return The ID
     */
    public int getID(String name) {
        return specIDs.get(name);
    }
}
