package atomicstryker.infernalmobs.common.network;

import atomicstryker.infernalmobs.common.network.packet.IPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Set;

/**
 * Helper class to wrap the 1.13 channels and packets into something
 * resembling a much older packet system. Create one instance of this for a Mod,
 * then use the helper methods to send Packets. Packet Handling is done inside
 * the packet classes themselves.
 *
 * @author AtomicStryker
 */
public class Network {

    private Network(){}

    private static SimpleChannel NETWORK;

    private static Set<Class<? extends IPacket>> registeredClasses;

    private static int packetId = 0;
    private static int id(){
        return packetId++;
    }

    /**
     * Creates an instance of the NetworkHelper with included channels for client and server communication.
     * Automatically registers the necessary channels and discriminators for the supplied Packet classes.
     *
     * @param channelName          channel name to use, anything but already taken designations goes
     * @param handledPacketClasses provide the IPacket classes you want to use for communication here
     */
    public static void register(String channelName, Set<Class<? extends IPacket>> handledPacketClasses) {

        SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(channelName)).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> "1.0.0")
                .simpleChannel();

        registeredClasses = handledPacketClasses;
        NETWORK = channel;

        for (Class<? extends IPacket> packetClass : handledPacketClasses) {
            try {
                IPacket instance = packetClass.getDeclaredConstructor().newInstance();
                channel.registerMessage(id(), packetClass, instance::encode, instance::decode, instance::handle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Sends the supplied Packet from a client to the server
     *
     * @param packet to send
     */
    public static void sendPacketToServer(IPacket packet) {
        checkIfPacketClassIsRegistered(packet.getClass());
        NETWORK.sendToServer(packet);
    }

    /**
     * Sends the supplied Packet from the server to the chosen Player
     *
     * @param packet to send
     * @param player to send to
     */
    public static void sendPacketToPlayer(IPacket packet, ServerPlayer player) {
        checkIfPacketClassIsRegistered(packet.getClass());
        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    /**
     * Sends a packet from the server to all currently connected players
     *
     * @param packet to send
     */
    public static void sendPacketToAllPlayers(IPacket packet) {
        checkIfPacketClassIsRegistered(packet.getClass());
        NETWORK.send(PacketDistributor.ALL.noArg(), packet);
    }

    /**
     * Sends a packet from the server to all players in a dimension around a location
     *
     * @param packet to send
     * @param tp     targetpoint instance to pass, cannot be null
     */
    public static void sendPacketToAllAroundPoint(IPacket packet, PacketDistributor.TargetPoint tp) {
        checkIfPacketClassIsRegistered(packet.getClass());
        NETWORK.send(PacketDistributor.NEAR.with(() -> tp), packet);
    }

    /**
     * Sends a packet from the server to all players in a dimension
     *
     * @param packet    to send
     * @param dimension serverside dim id to use
     */
    public static void sendPacketToAllInDimension(IPacket packet, ResourceKey<Level> dimension) {
        checkIfPacketClassIsRegistered(packet.getClass());
        NETWORK.send(PacketDistributor.DIMENSION.with(() -> dimension), packet);
    }

    /**
     * Since the crash that happens if we dont do this is complete garbage
     */
    private static void checkIfPacketClassIsRegistered(Class<? extends IPacket> clazz) {
        if (!registeredClasses.contains(clazz)) {
            throw new RuntimeException("NetworkHelper got unknown Packet type " + clazz + " to send, critical error");
        }
    }

}
