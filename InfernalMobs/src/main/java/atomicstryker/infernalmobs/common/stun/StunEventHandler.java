package atomicstryker.infernalmobs.common.stun;

import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.client.status.ClientStatus;
import atomicstryker.infernalmobs.common.network.PacketSender;
import atomicstryker.infernalmobs.common.stun.StunCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid = InfernalMobsCore.MOD_ID)
public class StunEventHandler {

    @SubscribeEvent
    public static void onAttachCapabilitiesToPlayers(AttachCapabilitiesEvent<Entity> event){
        if( event.getObject() instanceof Player){
            if( !event.getCapabilities().containsKey(StunCapabilityProvider.STUN_CAPABILITY)){
                event.addCapability(new ResourceLocation(InfernalMobsCore.MOD_ID, "properties"), new StunCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event){
        if(event.isWasDeath()){
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(StunCapabilityProvider.STUN_CAPABILITY).ifPresent( oldStore -> {
                event.getOriginal().getCapability(StunCapabilityProvider.STUN_CAPABILITY).ifPresent( newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if( event.side == LogicalSide.SERVER ){
            event.player.getCapability(StunCapabilityProvider.STUN_CAPABILITY).ifPresent( stunCapability -> {
                ServerPlayer player = (ServerPlayer) event.player;
                 if( stunCapability.isStunned() && stunCapability.getTicks() >= stunCapability.getDuration()){
                    stunCapability.removeStun();
                    PacketSender.sendStunPacket(player, false);
                } else if ( stunCapability.isStunned()){
                     stunCapability.tick();
                     player.teleportTo(stunCapability.getLockedPosition().x, stunCapability.getLockedPosition().y, stunCapability.getLockedPosition().z);
                     player.setXRot(stunCapability.getLockedRotation().x);
                     player.setYRot(stunCapability.getLockedRotation().y);
                     player.setDeltaMovement(0,0,0);
                     PacketSender.sendStunPacket(player, true);
                 }
            });
        }
    }
    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event){
        if(ClientStatus.isStunned()){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouseEvent(InputEvent.MouseButton event){
        if(ClientStatus.isStunned()){
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onCameraMove(ViewportEvent.ComputeCameraAngles event){
        if(ClientStatus.isStunned()){
            event.setCanceled(true);
        }
    }

}
