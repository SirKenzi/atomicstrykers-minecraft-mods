package atomicstryker.infernalmobs.common.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


/**
 * Packets only need to implement this and provide a PUBLIC constructor with no args,
 * unless you don't have constructors with >0 args. The class MUST also be
 * statically accessible, else you will suffer an InstantiationException!
 */
public interface IPacket {

    void encode(Object msg, FriendlyByteBuf packetBuffer);

    <MSG> MSG decode(FriendlyByteBuf packetBuffer);

    void handle(Object msg, Supplier<NetworkEvent.Context> contextSupplier);
}
