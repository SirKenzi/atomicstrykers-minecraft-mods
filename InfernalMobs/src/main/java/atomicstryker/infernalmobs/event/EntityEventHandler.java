package atomicstryker.infernalmobs.event;

import atomicstryker.infernalmobs.Cache;
import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.util.MobFarmGuard;
import atomicstryker.infernalmobs.util.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;

public class EntityEventHandler {

    @SubscribeEvent
    public void onEntityJoinedWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            String savedMods = event.getEntity().getPersistentData().getString(Tag.NBT_TAG.getId());
            if (!savedMods.isEmpty() && !savedMods.equals(InfernalMobsCore.instance().getNBTMarkerForNonInfernalEntities())) {
                InfernalMobsCore.instance().addModifiersToEntityFromString((LivingEntity) event.getEntity(), savedMods);
            } else {
                InfernalMobsCore.instance().processEntitySpawn((LivingEntity) event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public void onEntityLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().level.isClientSide) {
            InfernalMonster monster = Cache.getInfernalMonster(event.getEntity());
            if(Objects.nonNull(monster)){
                event.setCanceled(monster.handleDeath(event.getEntity()));
            }
        }
    }

    @SubscribeEvent
    public void onEntityLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
        if (!event.getEntity().level.isClientSide) {
            InfernalMonster monster = Cache.getInfernalMonster(event.getEntity());
            if(Objects.nonNull(monster)){
                monster.handleTargetChange(event.getTarget());
            }
        }
    }

    @SubscribeEvent
    public void onEntityLivingAttacked(LivingAttackEvent event) {
        /* fires both client and server before hurt, but we dont need this */
    }

    /**
     * Hook into EntityLivingHurt. Is always serverside, assured by mc itself
     */
    @SubscribeEvent
    public void onEntityLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() == event.getEntity()) {
            return;
        }
        this.onInfernalMonsterHurt(event);
        this.onPlayerHurt(event);
    }

    @SubscribeEvent
    public void onEntityLivingFall(LivingFallEvent event) {
        if (!event.getEntity().level.isClientSide) {
            InfernalMonster monster = Cache.getInfernalMonster(event.getEntity());
            if (Objects.nonNull(monster)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().level.isClientSide) {
            loadEntities(event);
            InfernalMonster monster = Cache.getInfernalMonster(event.getEntity());
            if( Objects.nonNull(monster)){
                monster.handleUpdate(event.getEntity());
                MobFarmGuard.detectMobFarm(event);
            }
        }
    }

    private void onInfernalMonsterHurt(LivingHurtEvent event){
        InfernalMonster monster = Cache.getInfernalMonster(event.getEntity());
        if( Objects.nonNull(monster)) {
            event.setAmount(monster.handleHurt(event.getEntity(), event.getSource(), event.getAmount()));
            MobFarmGuard.observeSuspiciousDamage(event);
        }
    }

    private void onPlayerHurt(LivingHurtEvent event){
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity)) {
            return;
        }
        InfernalMonster monster = Cache.getInfernalMonster((LivingEntity) attacker);
        if (Objects.nonNull(monster)) {
            event.setAmount(monster.handleAttack(event.getEntity(), event.getSource(), event.getAmount()));
        }
    }

    // workaround to get save-loaded infernal entities working, init them on their first living tick
    private void loadEntities(LivingEvent.LivingTickEvent event){
        if (event.getEntity().tickCount == 1) {
            String savedMods = event.getEntity().getPersistentData().getString(Tag.NBT_TAG.getId());
            if (!savedMods.isEmpty() && !savedMods.equals(InfernalMobsCore.instance().getNBTMarkerForNonInfernalEntities())) {
                InfernalMobsCore.instance().addModifiersToEntityFromString(event.getEntity(), savedMods);
            }
        }
    }
}
