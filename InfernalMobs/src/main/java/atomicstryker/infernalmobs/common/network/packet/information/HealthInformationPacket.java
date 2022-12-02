package atomicstryker.infernalmobs.common.network.packet.information;

import atomicstryker.infernalmobs.client.InfernalMobsClient;
import atomicstryker.infernalmobs.common.network.packet.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HealthInformationPacket implements IPacket {

    private int entityId;
    private float currentHealth;
    private float maxHealth;

    public HealthInformationPacket() {}

    public HealthInformationPacket( int entityId, float currentHealth, float maxHealth) {
        this.entityId = entityId;
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
    }

    @Override
    public void encode(Object msg, FriendlyByteBuf packetBuffer) {
        HealthInformationPacket healthPacket = (HealthInformationPacket) msg;
        packetBuffer.writeInt(healthPacket.entityId);
        packetBuffer.writeFloat(healthPacket.currentHealth);
        packetBuffer.writeFloat(healthPacket.maxHealth);
    }

    @Override
    public <MSG> MSG decode(FriendlyByteBuf packetBuffer) {
        HealthInformationPacket result = new HealthInformationPacket();
        result.entityId = packetBuffer.readInt();
        result.currentHealth = packetBuffer.readFloat();
        result.maxHealth = packetBuffer.readFloat();
        return (MSG) result;
    }

    @Override
    public void handle(Object msg, Supplier<NetworkEvent.Context> contextSupplier) {
        HealthInformationPacket healthInformationPacket = (HealthInformationPacket) msg;
        InfernalMobsClient.onHealthPacketForClient(healthInformationPacket.entityId, healthInformationPacket.currentHealth, healthInformationPacket.maxHealth);
    }}
