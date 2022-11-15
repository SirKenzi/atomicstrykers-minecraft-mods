package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;

public class MM_Sapper extends MobModifier {

    public MM_Sapper(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.SAPPER;
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && (source.getEntity() instanceof LivingEntity)
                && InfernalMobsCore.instance().getIsEntityAllowedTarget(source.getEntity())) {
            LivingEntity ent = (LivingEntity) source.getEntity();
            if (!ent.hasEffect(MobEffects.HUNGER)) {
                ent.addEffect(new MobEffectInstance(MobEffects.HUNGER, 120, 0));
            }
        }

        return super.onHurt(mob, source, damage);
    }

    @Override
    public float onAttack(LivingEntity entity, DamageSource source, float damage) {
        if (entity != null
                && InfernalMobsCore.instance().getIsEntityAllowedTarget(entity)
                && !entity.hasEffect(MobEffects.POISON)) {
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 120, 0));
        }

        return super.onAttack(entity, source, damage);
    }

}
