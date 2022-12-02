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

public class RequestMobModifiersInformationPacket implements IPacket {

    private int entityId;

    public RequestMobModifiersInformationPacket() {}

    public RequestMobModifiersInformationPacket(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void encode(Object msg, FriendlyByteBuf packetBuffer) {
        RequestMobModifiersInformationPacket healthPacket = (RequestMobModifiersInformationPacket) msg;
        packetBuffer.writeInt(healthPacket.entityId);
    }

    @Override
    public <MSG> MSG decode(FriendlyByteBuf packetBuffer) {
        RequestMobModifiersInformationPacket result = new RequestMobModifiersInformationPacket();
        result.entityId = packetBuffer.readInt();
        return (MSG) result;
    }

    @Override
    public void handle(Object msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            RequestMobModifiersInformationPacket requestMobModifiersPacket = (RequestMobModifiersInformationPacket) msg;
            ServerPlayer player = contextSupplier.get().getSender();
            if (Objects.isNull(player)) {
                return;
            }
            Entity entity = player.level.getEntity(requestMobModifiersPacket.entityId);
            if (!(entity instanceof LivingEntity)) {
                return;
            }
            InfernalMonster infernalMonster = Cache.getInfernalMonster((LivingEntity) entity);
            if (Objects.isNull(infernalMonster)) {
                return;
            }
            PacketSender.sendMobModifiersInformationPacketToPlayer(player, requestMobModifiersPacket.entityId, infernalMonster.getModifierNames());
            PacketSender.sendHealthInformationPacketToSurroundingPlayers(player);
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
