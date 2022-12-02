package atomicstryker.infernalmobs.event;

import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.stun.StunCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

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
        //todo: change to  remote
        if( event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.START){
            event.player.getCapability(StunCapabilityProvider.STUN_CAPABILITY).ifPresent( stunCapability -> {
                if( stunCapability.isStunned() && stunCapability.getTicks() >= stunCapability.getDuration()){
                    event.player.sendSystemMessage(Component.literal("PLAYER IS STUNNED"));
                } else {
                    stunCapability.removeStun();
                }
            });
        }
    }

    @SubscribeEvent
    public static void onMouseEvent(InputEvent.MouseButton event){
        LocalPlayer player = Minecraft.getInstance().player;
        if(Objects.nonNull(player)) {
            player.getCapability(StunCapabilityProvider.STUN_CAPABILITY).ifPresent(stunCapability -> {
                if (stunCapability.isStunned()) {
                    player.sendSystemMessage(Component.literal("PLAYER MOUSE IS STUNNED"));
                    event.setCanceled(true);
                }
            });
        }

    }
}
