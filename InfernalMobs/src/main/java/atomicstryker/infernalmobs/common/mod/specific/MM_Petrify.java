package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.InfernalEffects;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import atomicstryker.infernalmobs.common.sound.InfernalSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.stream.Collectors;

public class MM_Petrify extends MobModifier {

    private final static long cooldown = 210;
    private final static long reminderDelta = 45;
    private long nextAbilityUse = 0;
    private boolean reminderUsed = false;

    private long abilityRange = 12;

    public MM_Petrify(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    @Override
    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.PETRIFY;
    }

    @Override
    public boolean onUpdate(LivingEntity mob) {
        boolean isAbilityReady = isAbilityReady(); //we calculate this no matter if the entity has a valid target or not
        if (hasSteadyTarget() && getMobTarget() instanceof Player) {
            Player player = (Player) getMobTarget();
            if( isReminderReady()){
                player.getLevel().playSound(null, mob, InfernalSounds.MODIFIER_PETRIFY_REMINDER.get(), SoundSource.HOSTILE, 1f, 1f);
            }
            else if( isAbilityReady ){
                this.useAbility(player, mob);
            }
        }

        return super.onUpdate(mob);
    }

    private boolean isReminderReady(){
        if( nextAbilityUse > cooldown - reminderDelta && !reminderUsed) {
            reminderUsed = true;
            return true;
        }
        return false;
    }

    private boolean isAbilityReady(){
        if( nextAbilityUse++ > cooldown ) {
            nextAbilityUse = 0;
            reminderUsed = false;
            return true;
        }
        return false;
    }

    private void useAbility(Player player, LivingEntity source){
        player.getLevel().playSound(null, source, InfernalSounds.MODIFIER_PETRIFY_ABILITY.get(), SoundSource.HOSTILE, 1f, 1f);
        this.findAllEffectedEntities(source).stream().forEach( entity ->
            entity.addEffect(new MobEffectInstance(InfernalEffects.PETRIFY.get(), 120, 0))
        );

    }

    private List<LivingEntity> findAllEffectedEntities(LivingEntity source){
        return source.getLevel()
                .getEntities(source, source.getBoundingBox().inflate(15)).stream()
                .filter( entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter( entity -> isEntityLookingAtAbilitySource(entity, source))
                .collect(Collectors.toList());
    }

    private boolean isEntityLookingAtAbilitySource(Entity entity, Entity source){
        Vec3 entityPosition = entity.getPosition(1f);
        Vec3 entityDirection = entity.getLookAngle().scale(this.getAbilityRange());

        Vec3 pointA = entityPosition.add(entityDirection.z/2, 0, entityDirection.x/2);
        Vec3 pointB = entityPosition.add(-entityDirection.z/2, 0, -entityDirection.x/2);
        Vec3 pointC = pointA.add(entityDirection);
        Vec3 pointD = pointB.add(entityDirection);

        double minX = Math.min(Math.min(Math.min(pointA.x, pointB.x), pointC.x), pointD.x);
        double maxX = Math.max(Math.max(Math.max(pointA.x, pointB.x), pointC.x), pointD.x);
        double minZ = Math.min(Math.min(Math.min(pointA.z, pointB.z), pointC.z), pointD.z);
        double maxZ = Math.max(Math.max(Math.max(pointA.z, pointB.z), pointC.z), pointD.z);

        double sourceX = source.getBoundingBox().getCenter().x();
        double sourceZ = source.getBoundingBox().getCenter().z();

        return (minX < sourceX && sourceX < maxX) && (minZ < sourceZ && sourceZ < maxZ);
    }

    public long getAbilityRange(){ return this.abilityRange; }

    public void setAbilityRange(long abilityRange) {
        this.abilityRange = abilityRange;
    }
}