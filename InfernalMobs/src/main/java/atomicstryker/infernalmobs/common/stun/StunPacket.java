package atomicstryker.infernalmobs.common.stun;

import atomicstryker.infernalmobs.client.status.ClientStatus;
import atomicstryker.infernalmobs.common.network.packet.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StunPacket implements IPacket {

    boolean isStunned;

    public StunPacket(){}

    public StunPacket(boolean isStunned){
        this.isStunned = isStunned;
    }

    @Override
    public void encode(Object msg, FriendlyByteBuf packetBuffer) {
        StunPacket stunPacket = (StunPacket) msg;
        packetBuffer.writeBoolean(stunPacket.isStunned);
    }

    @Override
    public <MSG> MSG decode(FriendlyByteBuf packetBuffer) {
        StunPacket stunPacket = new StunPacket();
        stunPacket.isStunned = packetBuffer.readBoolean();
        return (MSG) stunPacket;
    }

    @Override
    public void handle(Object msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                StunPacket stunPacket = (StunPacket) msg;
                ClientStatus.setStunned(stunPacket.isStunned);
            })
        );
        contextSupplier.get().setPacketHandled(true);
    }
}
