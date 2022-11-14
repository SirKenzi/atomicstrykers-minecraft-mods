package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;

public class MM_Darkness extends MobModifier {

    public MM_Darkness() {
        super();
    }

    public MM_Darkness(MobModifier next) {
        super(next);
    }

    protected MobModifierType getMonsterModifierType() {
        return MobModifierType.DARKNESS;
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && (source.getEntity() instanceof LivingEntity livingEntity)
                && InfernalMobsCore.instance().getIsEntityAllowedTarget(source.getEntity())
                && !isCreativePlayer(livingEntity)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 120, 0));
        }

        return super.onHurt(mob, source, damage);
    }

    @Override
    public float onAttack(LivingEntity entity, DamageSource source, float damage) {
        if (entity != null
                && InfernalMobsCore.instance().getIsEntityAllowedTarget(entity)) {
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 120, 0));
        }

        return super.onAttack(entity, source, damage);
    }

}
