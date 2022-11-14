package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;

public class MM_Wither extends MobModifier {

    public MM_Wither() {
        super();
    }

    public MM_Wither(MobModifier next) {
        super(next);
    }

    protected MobModifierType getMonsterModifierType() {
        return MobModifierType.WITHER;
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && (source.getEntity() instanceof LivingEntity)
                && InfernalMobsCore.instance().getIsEntityAllowedTarget(source.getEntity())
                && !(source instanceof IndirectEntityDamageSource)) {
            ((LivingEntity) source.getEntity()).addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 0));
        }

        return super.onHurt(mob, source, damage);
    }

    @Override
    public float onAttack(LivingEntity entity, DamageSource source, float damage) {
        if (entity != null
                && InfernalMobsCore.instance().getIsEntityAllowedTarget(entity)) {
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 0));
        }

        return super.onAttack(entity, source, damage);
    }

}
