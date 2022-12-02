package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import atomicstryker.infernalmobs.common.mod.util.ChokedEntity;
import atomicstryker.infernalmobs.common.network.PacketSender;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MM_Choke extends MobModifier {

    private final List<ChokedEntity> chokedEntityList;

    public MM_Choke(InfernalMonster infernalMonster) {
        super(infernalMonster);
        this.chokedEntityList = new ArrayList<>();
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.CHOKE;
    }

    @Override
    public boolean onUpdate(LivingEntity mob) {

        ChokedEntity currentlyChokedEntity = this.findExistingChokedEntity(this.getMobTarget())
                .orElseGet(this::createNewChokedEntity);

        this.getChokedEntityList().removeIf( entity -> this.removeEntitiesWithMaxedAirSupply(entity, currentlyChokedEntity));
        this.updateAir();

        if (!hasSteadyTarget()) {
            return super.onUpdate(mob);
        }

        if( Objects.nonNull(currentlyChokedEntity) && this.canMobSeeTarget(mob, currentlyChokedEntity.getTarget())){
            if( !(currentlyChokedEntity.getTarget() instanceof Player && (((Player) currentlyChokedEntity.getTarget()).getAbilities().invulnerable))){
                currentlyChokedEntity.setAir(currentlyChokedEntity.getAir()-1);
                if( currentlyChokedEntity.getAir() < -19 ){
                    currentlyChokedEntity.setAir(0);
                    currentlyChokedEntity.getTarget().hurt(DamageSource.DROWN, 2.0F);
                }
            }
        }

        return super.onUpdate(mob);
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        this.findExistingChokedEntity(source.getEntity()).ifPresent( chokedEntity -> {
            int newAir = Math.min(chokedEntity.getAir() + 60, chokedEntity.getTarget().getMaxAirSupply());
            chokedEntity.setAir(newAir);
            updateAir();
        });
        return super.onHurt(mob, source, damage);
    }

    @Override
    public boolean onDeath(LivingEntity entity) {
        this.getChokedEntityList().forEach( chokedEntity -> {
            chokedEntity.setAir(chokedEntity.getTarget().getMaxAirSupply());
        });
        updateAir();
        return super.onDeath(entity);
    }

    private void updateAir() {
        this.getChokedEntityList().forEach( chokedEntity -> {
            chokedEntity.getTarget().setAirSupply(chokedEntity.getAir());
            if( chokedEntity.getTarget() instanceof ServerPlayer){
                PacketSender.sendAirPacket((ServerPlayer) chokedEntity.getTarget(), chokedEntity.getAir());
            }
        });
    }

    @Override
    public boolean shouldDropTarget(LivingEntity mob) {
        if(super.shouldDropTarget(mob) || Objects.isNull(super.getMobTarget())){
            return true;
        }
        if( super.getMobTarget().distanceTo(mob) < 5f ){
            return false;
        }
        boolean test = mob.hasLineOfSight(super.getMobTarget());
        return !test;
    }

    @Override
    public void onDropTarget() {
        super.onDropTarget();
    }

    private List<ChokedEntity> getChokedEntityList() {
        return chokedEntityList;
    }

    private Optional<ChokedEntity> findExistingChokedEntity(Entity livingEntity){
        return this.getChokedEntityList().stream()
                .filter(chokedEntity -> chokedEntity.getTarget().equals(livingEntity))
                .findAny();
    }


    private ChokedEntity createNewChokedEntity(){
        if( Objects.isNull(this.getMobTarget())){
            return null;
        }
        ChokedEntity newChokedEntity = new ChokedEntity(this.getMobTarget());
        this.getChokedEntityList().add(newChokedEntity);
        return newChokedEntity;
    }

    private boolean removeEntitiesWithMaxedAirSupply(ChokedEntity entity, ChokedEntity currentlyChokedEntity){
        if( entity.equals(currentlyChokedEntity)){
            return false;
        }
        entity.setAir(entity.getAir()+1);
        return entity.getAir() == entity.getTarget().getMaxAirSupply();
    }
}
