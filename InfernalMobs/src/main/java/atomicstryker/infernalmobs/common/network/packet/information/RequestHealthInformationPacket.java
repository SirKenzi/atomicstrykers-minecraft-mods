package atomicstryker.infernalmobs.common.network.packet.information;

import atomicstryker.infernalmobs.Cache;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.network.PacketSender;
import atomicstryker.infernalmobs.common.network.packet.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class RequestHealthInformationPacket implements IPacket {
    private int entityId;

    public RequestHealthInformationPacket() {}

    public RequestHealthInformationPacket(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void encode(Object msg, FriendlyByteBuf packetBuffer) {
        RequestHealthInformationPacket healthPacket = (RequestHealthInformationPacket) msg;
        packetBuffer.writeInt(healthPacket.entityId);
    }

    @Override
    public <MSG> MSG decode(FriendlyByteBuf packetBuffer) {
        RequestHealthInformationPacket result = new RequestHealthInformationPacket();
        result.entityId = packetBuffer.readInt();
        return (MSG) result;
    }

    @Override
    public void handle(Object msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            RequestHealthInformationPacket requestHealthInformationPacket = (RequestHealthInformationPacket) msg;
            ServerPlayer player = contextSupplier.get().getSender();
            if (Objects.isNull(player)) {
                return;
            }
            Entity entity = player.level.getEntity(requestHealthInformationPacket.entityId);
            if (!(entity instanceof LivingEntity)) {
                return;
            }
            InfernalMonster infernalMonster = Cache.getInfernalMonster((LivingEntity) entity);
            if (Objects.isNull(infernalMonster)) {
                return;
            }
            PacketSender.sendHealthInformationPacketToPlayer(player, requestHealthInformationPacket.entityId, ((LivingEntity) entity).getHealth(), ((LivingEntity) entity).getMaxHealth());
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
