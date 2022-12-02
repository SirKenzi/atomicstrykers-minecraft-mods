package atomicstryker.infernalmobs.common.network;

import atomicstryker.infernalmobs.common.network.packet.AirPacket;
import atomicstryker.infernalmobs.common.stun.StunPacket;
import atomicstryker.infernalmobs.common.network.packet.information.HealthInformationPacket;
import atomicstryker.infernalmobs.common.network.packet.information.MobModifiersInformationPacket;
import atomicstryker.infernalmobs.common.network.packet.information.RequestHealthInformationPacket;
import atomicstryker.infernalmobs.common.network.packet.KnockBackPacket;
import atomicstryker.infernalmobs.common.network.packet.VelocityPacket;
import atomicstryker.infernalmobs.common.network.packet.information.RequestMobModifiersInformationPacket;
import atomicstryker.infernalmobs.util.Helper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

public class PacketSender {

    private PacketSender(){}

    public static void sendVelocityPacket(ServerPlayer target, float xVel, float yVel, float zVel) {
        if (Helper.isEntityValidTarget(target)) {
            Network.sendPacketToPlayer(new VelocityPacket(xVel, yVel, zVel), target);
        }
    }

    public static void sendKnockBackPacket(ServerPlayer target, float xVel, float zVel) {
        if (Helper.isEntityValidTarget(target)) {
            Network.sendPacketToPlayer(new KnockBackPacket(xVel, zVel), target);
        }
    }

    public static void sendHealthInformationPacketToSurroundingPlayers(LivingEntity mob) {
        Network.sendPacketToAllAroundPoint(
                new HealthInformationPacket(mob.getId(), mob.getHealth(), mob.getMaxHealth()),
                new PacketDistributor.TargetPoint(mob.getX(), mob.getY(), mob.getZ(), 32d, mob.getCommandSenderWorld().dimension())
        );
    }

    public static void sendHealthInformationPacketToPlayer(ServerPlayer player, int entityId, float currentHealth, float maxHealth){
        Network.sendPacketToPlayer(new HealthInformationPacket(entityId, currentHealth, maxHealth), player);
    }

    public static void sendMobModifiersInformationPacketToPlayer(ServerPlayer player, int entityId, String mobModifiers){
        Network.sendPacketToPlayer(new MobModifiersInformationPacket(entityId, mobModifiers), player);
    }

    public static void requestHealthInformationFromServer(LivingEntity mob) {
        Network.sendPacketToServer(new RequestHealthInformationPacket(mob.getId()));
    }

    public static void requestMobModifiersInformationFromServer(LivingEntity mob) {
        Network.sendPacketToServer(new RequestMobModifiersInformationPacket(mob.getId()));
    }

    public static void sendStunPacket(ServerPlayer target, boolean isStunned){
        if (Helper.isEntityValidTarget(target)) {
            Network.sendPacketToPlayer(new StunPacket(isStunned), target);
        }
    }

    public static void sendAirPacket(ServerPlayer target, int lastAir) {
        if (Helper.isEntityValidTarget(target)) {
            Network.sendPacketToPlayer(new AirPacket(lastAir), target);
        }
    }


}
