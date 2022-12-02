package atomicstryker.infernalmobs.common.network.packet.information;

import atomicstryker.infernalmobs.client.InfernalMobsClient;
import atomicstryker.infernalmobs.common.network.packet.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MobModifiersInformationPacket implements IPacket {

    private int entityId;
    private String mobModifiers;

    public MobModifiersInformationPacket(){}

    public MobModifiersInformationPacket(int entityId, String mobModifiers){
        this.entityId = entityId;
        this.mobModifiers = mobModifiers;
    }

    @Override
    public void encode(Object msg, FriendlyByteBuf packetBuffer) {
        MobModifiersInformationPacket mobModifiersPacket = (MobModifiersInformationPacket) msg;
        packetBuffer.writeInt(mobModifiersPacket.entityId);
        packetBuffer.writeUtf(mobModifiersPacket.mobModifiers, 32767);
    }

    @Override
    public <MSG> MSG decode(FriendlyByteBuf packetBuffer) {
        MobModifiersInformationPacket result = new MobModifiersInformationPacket();
        result.entityId = packetBuffer.readInt();
        result.mobModifiers = packetBuffer.readUtf(32767);
        return (MSG) result;
    }

    @Override
    public void handle(Object msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    MobModifiersInformationPacket mobModifiersPacket = (MobModifiersInformationPacket) msg;
                    InfernalMobsClient.addModifiersToEntityFromString(mobModifiersPacket.entityId, mobModifiersPacket.mobModifiers);
                })
        );
        contextSupplier.get().setPacketHandled(true);
    }
}
